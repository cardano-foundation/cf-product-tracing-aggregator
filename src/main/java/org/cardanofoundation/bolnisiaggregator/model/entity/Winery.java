package org.cardanofoundation.bolnisiaggregator.model.entity;

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
@Table(name = "wineries")
@Slf4j
public class Winery {

    @Id
    @GeneratedValue
    private Long id;
    @Column(name = "winery_id", unique = true, nullable = false)
    private String wineryId;
}
