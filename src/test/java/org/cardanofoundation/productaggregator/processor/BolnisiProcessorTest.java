package org.cardanofoundation.productaggregator.processor;

import co.nstant.in.cbor.model.Map;
import co.nstant.in.cbor.model.UnicodeString;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.cardanofoundation.productaggregator.model.domain.NumberOfUnitsAndCerts;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class BolnisiProcessorTest {

    @InjectMocks
    BolnisiProcessor bolnisiProcessor;

    @Test
    void testCertMetadata() {
        Map metadata = new Map();
        metadata.put(new UnicodeString("t"), new UnicodeString("conformityCert"));
        NumberOfUnitsAndCerts numberofUnitsandCerts = bolnisiProcessor.processTransaction(metadata);

        assertEquals(1, numberofUnitsandCerts.getNumberOfCertificates());
        assertEquals(0, numberofUnitsandCerts.getNumberOfUnits());
    }

}
