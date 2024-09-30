package org.cardanofoundation.bolnisiaggregator.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import org.cardanofoundation.bolnisiaggregator.model.domain.AggregationDTO;
import org.cardanofoundation.bolnisiaggregator.model.entity.BolnisiAggregation;
import org.cardanofoundation.bolnisiaggregator.model.repository.BolnisiAggregationRepository;
import org.cardanofoundation.bolnisiaggregator.model.repository.WineryRepository;

@Component
@Slf4j
@RequiredArgsConstructor
public class MetaDataStorage {

    private final BolnisiAggregationRepository bolnisiAggregationRepository;
    private final WineryRepository wineryRepository;

    public void addAggregation(AggregationDTO aggregationDTO, Long slot) {

        BolnisiAggregation currentAgg = bolnisiAggregationRepository.findBolnisiAggregationWithMaxSlot()
                .orElse(new BolnisiAggregation());



        BolnisiAggregation bolnisiAggregation1 = new BolnisiAggregation(null,
                currentAgg.getNumberOfBottles() + aggregationDTO.getNumberOfBottles(),
                wineryRepository.countAll(),
                currentAgg.getNumberOfCertificates() + aggregationDTO.getNumberOfCertificates(),
                slot);
        bolnisiAggregationRepository.save(bolnisiAggregation1);
    }

    public int deleteBySlotGreaterThan(long slot) {
        return bolnisiAggregationRepository.deleteBySlotGreaterThan(slot);
    }
}
