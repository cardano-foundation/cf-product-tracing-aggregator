package org.cardanofoundation.bolnisiaggregator.model.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.cardanofoundation.bolnisiaggregator.model.entity.BolnisiAggregation;


public interface BolnisiAggregationRepository extends JpaRepository<BolnisiAggregation, Long> {


    @Query("""
            SELECT b FROM BolnisiAggregation b WHERE b.slot = (SELECT MAX(b.slot) FROM BolnisiAggregation b)
            """)
    Optional<BolnisiAggregation> findBolnisiAggregationWithMaxSlot();

    int deleteBySlotGreaterThan(long slot);
}
