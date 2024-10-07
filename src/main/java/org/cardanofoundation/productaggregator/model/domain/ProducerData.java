package org.cardanofoundation.productaggregator.model.domain;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProducerData {
    String publicKey;
    List<Lot> lots;
    boolean pkKeyVerified;
}
