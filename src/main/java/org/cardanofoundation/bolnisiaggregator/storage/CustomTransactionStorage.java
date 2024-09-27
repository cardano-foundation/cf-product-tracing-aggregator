package org.cardanofoundation.bolnisiaggregator.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cardanofoundation.bolnisiaggregator.model.entity.BolnisiAggregation;
import org.cardanofoundation.bolnisiaggregator.model.repository.BolnisiAggregationRepository;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class CustomTransactionStorage {

    private final BolnisiAggregationRepository bolnisiAggregationRepository;

    public void addBottles(int numBottles) {
        log.info("Adding {} bottles", numBottles);
        BolnisiAggregation bolnisiAggregation = getBolnisiAggregation();
        bolnisiAggregation.setNumberOfBottles(bolnisiAggregation.getNumberOfBottles() + numBottles);
        bolnisiAggregationRepository.save(bolnisiAggregation);
    }

    public void addWineries(int numWineries) {
        log.info("Adding {} wineries", numWineries);
        BolnisiAggregation bolnisiAggregation = getBolnisiAggregation();
        bolnisiAggregation.setNumberOfWineries(bolnisiAggregation.getNumberOfWineries() + numWineries);
        bolnisiAggregationRepository.save(bolnisiAggregation);
    }

    public void addCertificates(int numCertificates) {
        log.info("Adding {} certificates", numCertificates);
        BolnisiAggregation bolnisiAggregation = getBolnisiAggregation();
        bolnisiAggregation.setNumberOfCertificates(bolnisiAggregation.getNumberOfCertificates() + numCertificates);
        bolnisiAggregationRepository.save(bolnisiAggregation);
    }

    private BolnisiAggregation getBolnisiAggregation() {
        return bolnisiAggregationRepository.findById(1L).orElse(new BolnisiAggregation(1L, 0,0,0));
    }

}
