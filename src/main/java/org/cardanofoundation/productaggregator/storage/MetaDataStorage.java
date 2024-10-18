package org.cardanofoundation.productaggregator.storage;

import java.util.Set;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import org.cardanofoundation.productaggregator.model.domain.NumberOfUnitsAndCerts;
import org.cardanofoundation.productaggregator.model.entity.ProducerEntity;
import org.cardanofoundation.productaggregator.model.entity.ProductAggregationEntity;
import org.cardanofoundation.productaggregator.model.repository.ProducerRepository;
import org.cardanofoundation.productaggregator.model.repository.ProductAggregationRepository;

@Component
@Slf4j
@RequiredArgsConstructor
public class MetaDataStorage {

    private final ProductAggregationRepository productAggregationRepository;
    private final ProducerRepository producerRepository;

    public void addAggregation(NumberOfUnitsAndCerts numberofUnitsandCerts, Long slot) {

        ProductAggregationEntity currentAgg = productAggregationRepository.findProductAggregationWithMaxSlot()
                .orElse(new ProductAggregationEntity());
        Set<String> wineryIds = producerRepository.findAll().stream().map(ProducerEntity::getProducerId).collect(Collectors.toSet());
        ProductAggregationEntity productAggregationEntity1 = new ProductAggregationEntity(null,
                currentAgg.getNumberOfUnits() + numberofUnitsandCerts.getNumberOfUnits(),
                wineryIds.size(),
                currentAgg.getNumberOfCertificates() + numberofUnitsandCerts.getNumberOfCertificates(),
                slot);

        if(!currentAgg.equals(productAggregationEntity1)) {
            productAggregationRepository.save(productAggregationEntity1);
        } else {
            log.info("No change in aggregation data. Not saving");
        }
    }

    public int deleteBySlotGreaterThan(long slot) {
        return productAggregationRepository.deleteBySlotGreaterThan(slot);
    }
}
