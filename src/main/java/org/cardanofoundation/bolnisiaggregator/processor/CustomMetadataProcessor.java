package org.cardanofoundation.bolnisiaggregator.processor;

import co.nstant.in.cbor.model.DataItem;
import co.nstant.in.cbor.model.Map;
import co.nstant.in.cbor.model.UnsignedInteger;
import com.bloxbean.cardano.yaci.core.util.CborSerializationUtil;
import com.bloxbean.cardano.yaci.core.util.HexUtil;
import com.bloxbean.cardano.yaci.store.events.RollbackEvent;
import com.bloxbean.cardano.yaci.store.metadata.domain.TxMetadataEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.cardanofoundation.bolnisiaggregator.common.Constants;
import org.cardanofoundation.bolnisiaggregator.model.domain.AggregationDTO;
import org.cardanofoundation.bolnisiaggregator.storage.MetaDataStorage;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomMetadataProcessor {

    private final BolnisiProcessor bolnisiProcessor;
    private final MetaDataStorage metaDataStorage;

    @EventListener
    @Transactional
    public void handleMetadataEvent (TxMetadataEvent event) {
        event.getTxMetadataList().stream().filter(txMetadataLabel -> String.valueOf(Constants.METADATA_TAG).equals(txMetadataLabel.getLabel()))
            .forEach(txMetadataLabel -> {
                log.info("Processing Bolnisi Transaction: {}", txMetadataLabel.getTxHash());

                DataItem[] deserialize = CborSerializationUtil.deserialize(HexUtil.decodeHexString(txMetadataLabel.getCbor()));
                Map metadata = (Map)((Map) deserialize[0]).get(new UnsignedInteger(Constants.METADATA_TAG));
                Optional<AggregationDTO> optionalAggregationDTO = bolnisiProcessor.processTransaction(metadata);
                optionalAggregationDTO.ifPresent(aggregationDTO -> metaDataStorage.addAggregation(aggregationDTO, event.getEventMetadata().getSlot()));
            });
    }

    @EventListener
    @Transactional
    public void processRollBackEvent(RollbackEvent rollbackEvent) {
        log.info("Rollback event received. Rolling back transactions to slot {}", rollbackEvent.getRollbackTo().getSlot());
        metaDataStorage.deleteBySlotGreaterThan(rollbackEvent.getRollbackTo().getSlot());
    }

}
