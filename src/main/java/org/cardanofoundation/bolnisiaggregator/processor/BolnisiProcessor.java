package org.cardanofoundation.bolnisiaggregator.processor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import co.nstant.in.cbor.model.Map;
import co.nstant.in.cbor.model.UnicodeString;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.cardanofoundation.bolnisiaggregator.common.Constants;
import org.cardanofoundation.bolnisiaggregator.model.domain.AggregationDTO;
import org.cardanofoundation.bolnisiaggregator.model.domain.Cid;
import org.cardanofoundation.bolnisiaggregator.model.entity.Winery;
import org.cardanofoundation.bolnisiaggregator.model.repository.WineryRepository;

@Component
@RequiredArgsConstructor
@Slf4j
public class BolnisiProcessor {

    @Value("${bolnisi.resolver-url}")
    private String BOLNISI_RESOLVER_URL;

    private final RestTemplate restTemplate;
    private final WineryRepository wineryRepository;

    public AggregationDTO processTransaction(co.nstant.in.cbor.model.Map metadata) {

        AggregationDTO aggregationDTO = new AggregationDTO();

        String type = (metadata.get(new UnicodeString("t"))).toString();
        switch (type) {
            case Constants.SCM_TAG:
                handleSCMTag(metadata, aggregationDTO);
                break;
            case Constants.CERT_TAG:
                aggregationDTO.setNumberOfCertificates(1);
                break;
        }
        return aggregationDTO;
    }

    private void handleSCMTag(Map metadata, AggregationDTO aggregationDTO) {
        String rawData = ((UnicodeString) metadata.get(new UnicodeString(Constants.CID_TAG))).getString();
        java.util.Map<String, List<Cid>> offChainData = getOffChainData(rawData);

        wineryRepository.saveAll(offChainData.keySet().stream().map(Winery::new).collect(Collectors.toList()));

        int numberOfBottles = getSumOfBottlesForCID(offChainData);
        aggregationDTO.setNumberOfBottles(numberOfBottles);
    }


    @SneakyThrows
    private int getSumOfBottlesForCID(java.util.Map<String, List<Cid>> offchainData) {
        int sumBottles = 0;
        Set<String> keys = offchainData.keySet();
        for (String key : keys) {
            List<Cid> cids = offchainData.get(key);
            for (Cid cid : cids) {
                sumBottles += cid.getNumberOfBottles();
            }
        }
        return sumBottles;
    }

    @SneakyThrows
    private java.util.Map<String, List<Cid>> getOffChainData(String cid) {
        String resolverURL = BOLNISI_RESOLVER_URL + cid;
        ResponseEntity<String> forEntity = restTemplate.getForEntity(resolverURL, String.class);
        String offChainData = getOffChainData(forEntity);
        return new ObjectMapper().readValue(offChainData, new TypeReference<java.util.Map<String, List<Cid>>>() {
        });
    }


    private static String getOffChainData(ResponseEntity<String> forEntity) throws IOException {
        String minioURL = forEntity.getBody();
        assert minioURL != null;
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
