package org.cardanofoundation.productaggregator.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import org.cardanofoundation.productaggregator.BaseMapper;
import org.cardanofoundation.productaggregator.model.domain.ProductAggregationRecord;
import org.cardanofoundation.productaggregator.model.entity.ProductAggregationEntity;

@Mapper(config = BaseMapper.class)
public interface ProductAggregationMapper {

    @Mapping(source = "numberOfCertificates", target = "certificates")
    @Mapping(source = "numberOfProducers", target = "producers")
    @Mapping(source = "numberOfUnits", target = "units")
    ProductAggregationRecord toProductAggregationRecord(ProductAggregationEntity productAggregationRecord);

}
