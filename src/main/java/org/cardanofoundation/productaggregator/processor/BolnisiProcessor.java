package org.cardanofoundation.productaggregator.processor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import co.nstant.in.cbor.model.Array;
import co.nstant.in.cbor.model.ByteString;
import co.nstant.in.cbor.model.UnicodeString;
import com.bloxbean.cardano.client.util.HexUtil;
import com.bloxbean.cardano.client.util.JsonUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.cardanofoundation.productaggregator.common.Constants;
import org.cardanofoundation.productaggregator.common.CryptoUtil;
import org.cardanofoundation.productaggregator.model.domain.Lot;
import org.cardanofoundation.productaggregator.model.domain.NumberOfUnitsAndCerts;
import org.cardanofoundation.productaggregator.model.domain.ProducerData;
import org.cardanofoundation.productaggregator.model.entity.Producer;
import org.cardanofoundation.productaggregator.model.repository.ProducerRepository;

@Component
@RequiredArgsConstructor
@Slf4j
public class BolnisiProcessor {

    @Value("${product-tracing.resolver-url}")
    private String bolnisiResolverUrl;

    @Value("${product-tracing.public-key-url}")
    private String bolnisiPublicKeyUrl;

    @Value("${product-tracing.public-key-replacer}")
    private String publicKeyReplacer;

    private final RestTemplate restTemplate;

    private final ProducerRepository producerRepository;

    public NumberOfUnitsAndCerts processTransaction(co.nstant.in.cbor.model.Map metadata) {

        NumberOfUnitsAndCerts numberofUnitsandCerts = new NumberOfUnitsAndCerts();

        String type = (metadata.get(new UnicodeString("t"))).toString();
        switch (type) {
            case Constants.SCM_TAG:
                int numberOfBottles = getNumberOfBottlesForSCMTag(metadata);
                numberofUnitsandCerts.setNumberOfUnits(numberOfBottles);
                break;
            case Constants.CERT_TAG:
                numberofUnitsandCerts.setNumberOfCertificates(1);
                break;
            default:
                throw new IllegalArgumentException("Unknown transaction type: " + type);
        }
        return numberofUnitsandCerts;
    }

    /**
     * Verifies the signature of the lots and returns the number of bottles
     * Verification process involves following steps:
     * 1. Verify CID of offchain data with the CID in the metadata
     * 2. Verify Public Key of each winery with the offchain public key
     * 3. Verify signature of each lot with the public key of the winery
     * @param metadata cbor decoded metadata
     * @return number of bottles
     */
    @SneakyThrows
    private int getNumberOfBottlesForSCMTag(co.nstant.in.cbor.model.Map metadata) {

        String cid = ((UnicodeString) metadata.get(new UnicodeString(Constants.CID_TAG))).getString();
        String rawOffChainData = getOffChainData(cid);
        if(!CryptoUtil.verifyCID(cid, rawOffChainData)) {
            log.error("CID verification failed for CID: {}", cid);
            return 0;
        }
        HashMap offChainData = new ObjectMapper().readValue(rawOffChainData, HashMap.class);

        Map<String, ProducerData> wineries = mapMetadataAndOffChainDataToObject(metadata, offChainData);

        // Verifying the signature
        removeWineriesWithWrongPK(wineries);
        verifyLotSignature(wineries);

        // saving all Wineries, these will be used to calculate the total number of wineries
        saveWineries(wineries.keySet());
        return getSumOfBottlesForCID(wineries);
    }

    private void saveWineries(Set<String> producers) {
        producers.forEach(producerId -> {
            try {
                producerRepository.flush();
                Optional<List<Producer>> byId = producerRepository.findByProducerId(producerId);
                if(byId.isPresent()) {
                    log.info("Winery with ID: {} already exists", producerId);
                    return;
                }
                producerRepository.saveAndFlush(Producer.builder().producerId(producerId).build());
            } catch (Exception e) {
                log.error("Error saving winery with ID: {}", producerId, e);
            }
        });
    }

    private static Map<String, ProducerData> mapMetadataAndOffChainDataToObject(co.nstant.in.cbor.model.Map metadata, HashMap offChainData) {
        Map<String, ProducerData> wineries = new HashMap<>();
        co.nstant.in.cbor.model.Map publicKeys = (co.nstant.in.cbor.model.Map) metadata.get(new UnicodeString("d"));
        publicKeys.getKeys().forEach(key -> {
            if(!offChainData.containsKey(key.toString())) {
                log.info("Winery ID: {} doesn't have offchain data", key);
            }
            co.nstant.in.cbor.model.Map wineryKeys = (co.nstant.in.cbor.model.Map) publicKeys.get(key);

            Array array = (Array) wineryKeys.get(new UnicodeString("s"));
            List<Lot> lots = new ArrayList<>();
            for(int i = 0; i < array.getDataItems().size(); i++) {
                ByteString signatureBytes = (ByteString) array.getDataItems().get(i);
                String signature = HexUtil.encodeHexString(signatureBytes.getBytes());

                LinkedHashMap<String, Object> linkedHashMap = ((ArrayList<LinkedHashMap<String, Object>>) offChainData.get(key.toString())).get(i);
                // avoiding to add the same lot again, since there can be duplicates within the metadata
                if(lots.stream().filter(lot -> lot.getSignature().equals(signature)).toList().isEmpty()) {
                    Lot lot = new Lot(signature, (int)linkedHashMap.get(Constants.NUMBER_OF_BOTTLES), false, linkedHashMap);
                    lots.add(lot);
                }

            }
            String wineryPublicKey = getPublicKey(wineryKeys);
            wineries.put(key.toString(), new ProducerData(wineryPublicKey, lots, false));
        });
        return wineries;
    }

    private void verifyLotSignature(java.util.Map<String, ProducerData> wineries) {
        for (java.util.Map.Entry<String, ProducerData> wineryEntry : wineries.entrySet()) {

            ProducerData value = wineryEntry.getValue();
            for (Lot lot : value.getLots()) {
                boolean isLotSignatureValid = CryptoUtil.verifySignatureWithEd25519(value.getPublicKey(), lot.getSignature(), JsonUtil.getPrettyJson(lot.getRawOffChainData()));
                lot.setValid(isLotSignatureValid);
            }
        }
    }

    private void removeWineriesWithWrongPK(java.util.Map<String, ProducerData> wineries) throws IOException {
        for (java.util.Map.Entry<String, ProducerData> wineryEntry : wineries.entrySet()) {
            String offChainPublicKey = getOffChainPublicKey(wineryEntry.getKey());
            if(wineryEntry.getValue().getPublicKey().equals(offChainPublicKey)) {
                ProducerData value = wineryEntry.getValue();
                value.setPkKeyVerified(true);
                wineries.put(wineryEntry.getKey(), value);
            }
            else {
                log.info("Public key doesn't match removing ID: {}", wineryEntry.getKey());
                wineries.remove(wineryEntry.getKey());
            }
        }
    }

    private String getOffChainPublicKey(String wineryID) throws IOException {
        URL url = new URL(bolnisiPublicKeyUrl.replace("{" + publicKeyReplacer + "}", wineryID));
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        byte[] bytes = conn.getInputStream().readAllBytes();
        return HexUtil.encodeHexString(bytes);
    }

    private static String getPublicKey(co.nstant.in.cbor.model.Map wineryMetadata) {
        ByteString pk = (ByteString) wineryMetadata.get(new UnicodeString("pk"));
        return HexUtil.encodeHexString(pk.getBytes());
    }

    @SneakyThrows
    private int getSumOfBottlesForCID(java.util.Map<String, ProducerData> offchainData) {
        int sumBottles = 0;
        for (java.util.Map.Entry<String, ProducerData> wineryDataEntry : offchainData.entrySet()) {
            for (Lot lot : wineryDataEntry.getValue().getLots()) {

                if (lot.isValid()) {
                    sumBottles += lot.getNumberOfUnits();
                }
            }
        }
        return sumBottles;
    }

    @SneakyThrows
    public String getOffChainData(String cid) {
        String resolverURL = bolnisiResolverUrl + cid;
        ResponseEntity<String> forEntity = restTemplate.getForEntity(resolverURL, String.class);
        return getOffChainDataFromMinio(forEntity.getBody());
    }


    private String getOffChainDataFromMinio(String minioURL) throws IOException {
        URL url = new URL(minioURL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        StringBuilder result = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream()))) {
            for (String line; (line = reader.readLine()) != null; ) {
                result.append(line);
            }
        }
        return result.toString();
    }
}
