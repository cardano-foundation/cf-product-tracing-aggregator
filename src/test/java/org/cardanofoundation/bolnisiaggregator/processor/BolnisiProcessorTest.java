package org.cardanofoundation.bolnisiaggregator.processor;

import co.nstant.in.cbor.model.Array;
import co.nstant.in.cbor.model.Map;
import co.nstant.in.cbor.model.UnicodeString;
import co.nstant.in.cbor.model.UnsignedInteger;
import org.cardanofoundation.bolnisiaggregator.model.domain.AggregationDTO;
import org.cardanofoundation.bolnisiaggregator.model.domain.Cid;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BolnisiProcessorTest {

    @InjectMocks
    BolnisiProcessor bolnisiProcessor;

    @Test
    void testCertMetadata() {
        Map metadata = new Map();
        metadata.put(new UnicodeString("t"), new UnicodeString("conformityCert"));
        AggregationDTO aggregationDTO = bolnisiProcessor.processTransaction(metadata);

        assertEquals(1, aggregationDTO.getNumberOfCertificates());
        assertEquals(0, aggregationDTO.getNumberOfBottles());
    }

}
