package org.cardanofoundation.bolnisiaggregator.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "bolnisi_aggregation")
@Slf4j
@EqualsAndHashCode(exclude = {"id", "slot"})
public class BolnisiAggregation {

    @Id
    @GeneratedValue
    private Long id;
    int numberOfBottles;
    int numberOfWineries;
    int numberOfCertificates;
    private Long slot;

}
