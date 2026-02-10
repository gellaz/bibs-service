package it.bibs.store;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StoreMapper {

  StoreDTO updateStoreDTO(Store store, @MappingTarget StoreDTO storeDTO);

  @Mapping(target = "id", ignore = true)
  Store updateStore(StoreDTO storeDTO, @MappingTarget Store store);
}
