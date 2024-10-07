package org.cardanofoundation.productaggregator.storage;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;
import com.bloxbean.cardano.yaci.store.metadata.domain.TxMetadataLabel;
import com.bloxbean.cardano.yaci.store.metadata.storage.impl.TxMetadataStorageImpl;
import com.bloxbean.cardano.yaci.store.metadata.storage.impl.mapper.MetadataMapper;
import com.bloxbean.cardano.yaci.store.metadata.storage.impl.repository.TxMetadataLabelRepository;

@Component
public class CustomMetadataStorage extends TxMetadataStorageImpl {
    public CustomMetadataStorage(TxMetadataLabelRepository metadataLabelRepository, MetadataMapper metadataMapper) {
        super(metadataLabelRepository, metadataMapper);
    }

    @Override
    public List<TxMetadataLabel> saveAll(List<TxMetadataLabel> txMetadataLabels) {
        // avoiding to save metadata
        return Collections.emptyList();
    }
}
