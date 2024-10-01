package org.cardanofoundation.bolnisiaggregator.storage;

import java.util.Set;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import org.cardanofoundation.bolnisiaggregator.model.domain.NumberOfBottlesAndCerts;
import org.cardanofoundation.bolnisiaggregator.model.entity.BolnisiAggregation;
import org.cardanofoundation.bolnisiaggregator.model.entity.Winery;
import org.cardanofoundation.bolnisiaggregator.model.repository.BolnisiAggregationRepository;
import org.cardanofoundation.bolnisiaggregator.model.repository.WineryRepository;

@Component
@Slf4j
@RequiredArgsConstructor
public class MetaDataStorage {

    private final BolnisiAggregationRepository bolnisiAggregationRepository;
    private final WineryRepository wineryRepository;

    public void addAggregation(NumberOfBottlesAndCerts numberOfBottlesAndCerts, Long slot) {

        BolnisiAggregation currentAgg = bolnisiAggregationRepository.findBolnisiAggregationWithMaxSlot()
                .orElse(new BolnisiAggregation());
        Set<String> wineryIds = wineryRepository.findAll().stream().map(Winery::getWineryId).collect(Collectors.toSet());
        BolnisiAggregation bolnisiAggregation1 = new BolnisiAggregation(null,
                currentAgg.getNumberOfBottles() + numberOfBottlesAndCerts.getNumberOfBottles(),
                wineryIds.size(),
                currentAgg.getNumberOfCertificates() + numberOfBottlesAndCerts.getNumberOfCertificates(),
                slot);

        if(!currentAgg.equals(bolnisiAggregation1)) {
            bolnisiAggregationRepository.save(bolnisiAggregation1);
        } else {
            log.info("No change in aggregation data. Not saving");
        }
    }

    public int deleteBySlotGreaterThan(long slot) {
        return bolnisiAggregationRepository.deleteBySlotGreaterThan(slot);
    }
}
