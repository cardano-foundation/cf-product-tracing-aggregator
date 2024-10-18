package org.cardanofoundation.productaggregator.service;

import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import org.cardanofoundation.productaggregator.model.domain.ProductAggregationRecord;
import org.cardanofoundation.productaggregator.model.mapper.ProductAggregationMapper;
import org.cardanofoundation.productaggregator.model.repository.ProductAggregationRepository;

@Component
@RequiredArgsConstructor
public class ProductTracingService {

    private final ProductAggregationRepository productAggregationRepository;

    private final ProductAggregationMapper productAggregationMapper;

    public Optional<ProductAggregationRecord> getLatestProductAggregation() {
        return productAggregationRepository.findProductAggregationWithMaxSlot().map(productAggregationMapper::toProductAggregationRecord);
    }

    public Optional<ProductAggregationRecord> getProductAggregationForSlot(Long slot) {
        return productAggregationRepository.findProductAggregationEntityBySlot(slot).map(productAggregationMapper::toProductAggregationRecord);
    }

    public List<ProductAggregationRecord> getAllProductAggregations(Pageable pageable) {
        return productAggregationRepository.findAll(pageable).map(productAggregationMapper::toProductAggregationRecord).toList();
    }
}
