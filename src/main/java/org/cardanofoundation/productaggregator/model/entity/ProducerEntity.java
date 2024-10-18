package org.cardanofoundation.productaggregator.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "producers")
@Slf4j
public class ProducerEntity {

    @Id
    @GeneratedValue
    private Long id;
    @Column(name = "producer_id", nullable = false)
    private String producerId;
}
