package org.cardanofoundation.bolnisiaggregator.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cardanofoundation.bolnisiaggregator.model.domain.AggregationDTO;
import org.cardanofoundation.bolnisiaggregator.model.entity.BolnisiAggregation;
import org.cardanofoundation.bolnisiaggregator.model.repository.BolnisiAggregationRepository;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class MetaDataStorage {

    private final BolnisiAggregationRepository bolnisiAggregationRepository;

    private BolnisiAggregation getBolnisiAggregation() {
        return bolnisiAggregationRepository.findBolnisiAggregationWithMaxSlot().orElse(new BolnisiAggregation());
    }

    public void addAggregation(AggregationDTO aggregationDTO, Long slot) {

        BolnisiAggregation currentAgg = getBolnisiAggregation();
        BolnisiAggregation bolnisiAggregation1 = new BolnisiAggregation(null,
                currentAgg.getNumberOfBottles() + aggregationDTO.getNumberOfBottles(),
                currentAgg.getNumberOfWineries() + aggregationDTO.getNumberOfWineries(),
                currentAgg.getNumberOfCertificates() + aggregationDTO.getNumberOfCertificates(),
                slot);
        bolnisiAggregationRepository.save(bolnisiAggregation1);
//        }
    }

    public int deleteBySlotGreaterThan(long slot) {
        return bolnisiAggregationRepository.deleteBySlotGreaterThan(slot);
    }
}
