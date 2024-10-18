package org.cardanofoundation.productaggregator.model.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.cardanofoundation.productaggregator.model.entity.ProductAggregationEntity;


public interface ProductAggregationRepository extends JpaRepository<ProductAggregationEntity, Long> {


    @Query("""
            SELECT b FROM ProductAggregationEntity b WHERE b.slot = (SELECT MAX(b.slot) FROM ProductAggregationEntity b)
            """)
    Optional<ProductAggregationEntity> findProductAggregationWithMaxSlot();

    int deleteBySlotGreaterThan(long slot);

    Optional<ProductAggregationEntity> findProductAggregationEntityBySlot(Long slot);
}
