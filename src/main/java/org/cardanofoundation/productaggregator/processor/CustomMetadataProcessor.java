package org.cardanofoundation.productaggregator.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import co.nstant.in.cbor.model.DataItem;
import co.nstant.in.cbor.model.Map;
import co.nstant.in.cbor.model.UnsignedInteger;
import com.bloxbean.cardano.yaci.core.util.CborSerializationUtil;
import com.bloxbean.cardano.yaci.core.util.HexUtil;
import com.bloxbean.cardano.yaci.store.events.RollbackEvent;
import com.bloxbean.cardano.yaci.store.metadata.domain.TxMetadataEvent;

import org.cardanofoundation.productaggregator.common.Constants;
import org.cardanofoundation.productaggregator.model.domain.NumberOfUnitsAndCerts;
import org.cardanofoundation.productaggregator.storage.MetaDataStorage;


@Component
@RequiredArgsConstructor
@Slf4j
public class CustomMetadataProcessor {

    private final ProductProcessor productProcessor;
    private final MetaDataStorage metaDataStorage;

    @EventListener
    @Transactional
    public void handleMetadataEvent (TxMetadataEvent event) {
        event.getTxMetadataList().stream()
                .filter(txMetadataLabel -> String.valueOf(Constants.METADATA_TAG).equals(txMetadataLabel.getLabel()))
                .forEach(txMetadataLabel -> {
                    log.info("Processing Transaction: {}", txMetadataLabel.getTxHash());

                    DataItem[] deserialize = CborSerializationUtil.deserialize(HexUtil.decodeHexString(txMetadataLabel.getCbor()));
                    Map metadata = (Map)((Map) deserialize[0]).get(new UnsignedInteger(Constants.METADATA_TAG));

                    NumberOfUnitsAndCerts numberofUnitsandCerts = productProcessor.processTransaction(metadata);
                    metaDataStorage.addAggregation(numberofUnitsandCerts, event.getEventMetadata().getSlot());
            });
    }

    @EventListener
    @Transactional
    public void processRollBackEvent(RollbackEvent rollbackEvent) {
        log.info("Rollback event received. Rolling back transactions to slot {}", rollbackEvent.getRollbackTo().getSlot());
        metaDataStorage.deleteBySlotGreaterThan(rollbackEvent.getRollbackTo().getSlot());
    }

}
