package org.cardanofoundation.bolnisiaggregator.model.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.cardanofoundation.bolnisiaggregator.model.entity.Winery;

public interface WineryRepository extends JpaRepository<Winery, Long> {

    @Query("SELECT COUNT(w) FROM Winery w")
    int countAll();

    Optional<List<Winery>> findByWineryId(String wineryId);

}
