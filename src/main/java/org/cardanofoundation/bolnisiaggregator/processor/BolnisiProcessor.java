package org.cardanofoundation.bolnisiaggregator.processor;

import co.nstant.in.cbor.model.UnicodeString;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.cardanofoundation.bolnisiaggregator.common.Constants;
import org.cardanofoundation.bolnisiaggregator.model.domain.AggregationDTO;
import org.cardanofoundation.bolnisiaggregator.model.domain.Cid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class BolnisiProcessor {

    @Value("${bolnisi.resolver-url}")
    private String BOLNISI_RESOLVER_URL;

    private final RestTemplate restTemplate;

    public Optional<AggregationDTO> processTransaction(co.nstant.in.cbor.model.Map metadata) {

        AggregationDTO aggregationDTO = new AggregationDTO();

        UnicodeString type = (UnicodeString) metadata.get(new UnicodeString("t"));
        if(type.getString().equals(Constants.SCM_TAG)) {
            String rawData = ((UnicodeString) metadata.get(new UnicodeString(Constants.CID_TAG))).getString();
            java.util.Map<String, List<Cid>> offChainData = getOffChainData(rawData);

            int numberOfBottles = getSumOfBottlesForCID(offChainData);
            aggregationDTO.setNumberOfBottles(numberOfBottles);

        }
        return Optional.of(aggregationDTO);
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
        int sumBottles = 0;
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
        String string = result.toString();
        return string;
    }

}
