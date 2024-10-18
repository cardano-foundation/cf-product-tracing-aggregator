package org.cardanofoundation.productaggregator.model.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.cardanofoundation.productaggregator.model.entity.ProducerEntity;

public interface ProducerRepository extends JpaRepository<ProducerEntity, Long> {

    @Query("SELECT COUNT(w) FROM ProducerEntity w")
    int countAll();

    Optional<List<ProducerEntity>> findByProducerId(String producerId);

}
