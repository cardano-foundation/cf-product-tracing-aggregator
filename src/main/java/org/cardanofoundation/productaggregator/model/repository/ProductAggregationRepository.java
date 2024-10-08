package org.cardanofoundation.productaggregator.model.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.cardanofoundation.productaggregator.model.entity.ProductAggregation;


public interface ProductAggregationRepository extends JpaRepository<ProductAggregation, Long> {


    @Query("""
            SELECT b FROM ProductAggregation b WHERE b.slot = (SELECT MAX(b.slot) FROM ProductAggregation b)
            """)
    Optional<ProductAggregation> findProductAggregationWithMaxSlot();

    int deleteBySlotGreaterThan(long slot);
}
