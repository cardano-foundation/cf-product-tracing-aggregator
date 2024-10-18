package org.cardanofoundation.productaggregator.model.domain;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record ProductAggregationRecord(Long certificates, Long producers, Long units, Long slot) {
}
