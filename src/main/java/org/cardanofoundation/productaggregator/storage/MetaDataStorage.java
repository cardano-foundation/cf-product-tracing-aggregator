package org.cardanofoundation.productaggregator.storage;

import java.util.Set;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import org.cardanofoundation.productaggregator.model.domain.NumberOfUnitsAndCerts;
import org.cardanofoundation.productaggregator.model.entity.ProductAggregation;
import org.cardanofoundation.productaggregator.model.entity.Producer;
import org.cardanofoundation.productaggregator.model.repository.ProductAggregationRepository;
import org.cardanofoundation.productaggregator.model.repository.ProducerRepository;

@Component
@Slf4j
@RequiredArgsConstructor
public class MetaDataStorage {

    private final ProductAggregationRepository productAggregationRepository;
    private final ProducerRepository producerRepository;

    public void addAggregation(NumberOfUnitsAndCerts numberofUnitsandCerts, Long slot) {

        ProductAggregation currentAgg = productAggregationRepository.findBolnisiAggregationWithMaxSlot()
                .orElse(new ProductAggregation());
        Set<String> wineryIds = producerRepository.findAll().stream().map(Producer::getProducerId).collect(Collectors.toSet());
        ProductAggregation productAggregation1 = new ProductAggregation(null,
                currentAgg.getNumberOfUnits() + numberofUnitsandCerts.getNumberOfUnits(),
                wineryIds.size(),
                currentAgg.getNumberOfCertificates() + numberofUnitsandCerts.getNumberOfCertificates(),
                slot);

        if(!currentAgg.equals(productAggregation1)) {
            productAggregationRepository.save(productAggregation1);
        } else {
            log.info("No change in aggregation data. Not saving");
        }
    }

    public int deleteBySlotGreaterThan(long slot) {
        return productAggregationRepository.deleteBySlotGreaterThan(slot);
    }
}
