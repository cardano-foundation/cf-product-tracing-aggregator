package org.cardanofoundation.bolnisiaggregator.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "bolnisi_aggregation")
public class BolnisiAggregation {

    @Id
    private Long id;
    int numberOfBottles;
    int numberOfWineries;
    int numberOfCertificates;

}
