package org.cardanofoundation.bolnisiaggregator.processor;

import co.nstant.in.cbor.model.DataItem;
import co.nstant.in.cbor.model.Map;
import co.nstant.in.cbor.model.UnicodeString;
import com.bloxbean.cardano.client.util.HexUtil;
import com.bloxbean.cardano.yaci.core.util.CborSerializationUtil;
import com.bloxbean.cardano.yaci.store.events.TransactionEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.cardanofoundation.bolnisiaggregator.storage.CustomTransactionStorage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomTransactionProcessor {

    @Value("${bolnisi.resolver-url}")
    private String BOLNISI_RESOLVER_URL;
    private final RestTemplate restTemplate;
    private final CustomTransactionStorage customTransactionStorage;

    @EventListener
    @Transactional
    public void processTransactionEvent(TransactionEvent event) {
        event.getTransactions().forEach(transaction -> {
            if(transaction.getAuxData() == null || transaction.getAuxData().getMetadataCbor() == null) {
                return;
            }
            DataItem[] deserialize = CborSerializationUtil.deserialize(HexUtil.decodeHexString(transaction.getAuxData().getMetadataCbor()));
            Map map = (Map) deserialize[0];
            map.getKeys().forEach(key -> {
                if(key.toString().equals("1904")) {
                    Map m = (Map) map.get(key);
                    UnicodeString type = (UnicodeString) m.get(new UnicodeString("t"));
                    if(type.getString().equals("scm")) {
                        int numberOfBottles = getSumOfBottlesForCID(((UnicodeString) m.get(new UnicodeString("cid"))).getString());
                        customTransactionStorage.addBottles(numberOfBottles);
                    }
                    log.info("Bolnisi Transaction Hash - " + transaction.getTxHash());
                }
            });
        });
    }

    @SneakyThrows
    private int getSumOfBottlesForCID(String cid) {
        String resolverURL = BOLNISI_RESOLVER_URL + cid;
        ResponseEntity<String> forEntity = restTemplate.getForEntity(resolverURL, String.class);
        String minioURL = forEntity.getBody();
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
        java.util.Map<String, List<java.util.Map<String, Object>>> cids = new ObjectMapper().readValue(string, java.util.HashMap.class);
        Set<String> set = cids.keySet();
        int sumBottles = 0;
        for (String key : set) {
            List<java.util.Map<String, Object>> maps = cids.get(key);
            for (java.util.Map<String, Object> map : maps) {
                sumBottles += (int) map.get("number_of_bottles");
            }
        }
        return sumBottles;
    }

}
