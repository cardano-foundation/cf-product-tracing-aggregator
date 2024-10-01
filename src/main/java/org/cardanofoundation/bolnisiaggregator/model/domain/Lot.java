package org.cardanofoundation.bolnisiaggregator.model.domain;

import java.util.LinkedHashMap;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Lot {

    private String signature;
    private int numberOfBottles;
    private boolean isValid;
    private LinkedHashMap<String, Object> rawOffChainData;
}
