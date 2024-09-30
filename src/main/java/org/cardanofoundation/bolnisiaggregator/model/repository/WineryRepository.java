package org.cardanofoundation.bolnisiaggregator.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.cardanofoundation.bolnisiaggregator.model.entity.Winery;

public interface WineryRepository extends JpaRepository<Winery, String> {

    @Query("SELECT COUNT(w) FROM Winery w")
    int countAll();
}
