package org.cardanofoundation.productaggregator.model.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.cardanofoundation.productaggregator.model.entity.Producer;

public interface ProducerRepository extends JpaRepository<Producer, Long> {

    @Query("SELECT COUNT(w) FROM Producer w")
    int countAll();

    Optional<List<Producer>> findByProducerId(String producerId);

}
