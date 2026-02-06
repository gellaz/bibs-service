package it.bibs.store;

import java.util.HashSet;
import java.util.List;

import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import it.bibs.user.User;
import it.bibs.user.UserRepository;
import it.bibs.util.NotFoundException;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StoreMapper {

  @Mapping(target = "user", ignore = true)
  StoreDTO updateStoreDTO(Store store, @MappingTarget StoreDTO storeDTO);

  @AfterMapping
  default void afterUpdateStoreDTO(Store store, @MappingTarget StoreDTO storeDTO) {
    storeDTO.setUser(store.getUser().stream().map(user -> user.getId()).toList());
  }

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "user", ignore = true)
  Store updateStore(
      StoreDTO storeDTO, @MappingTarget Store store, @Context UserRepository userRepository);

  @AfterMapping
  default void afterUpdateStore(
      StoreDTO storeDTO, @MappingTarget Store store, @Context UserRepository userRepository) {
    final List<User> user =
        userRepository.findAllById(storeDTO.getUser() == null ? List.of() : storeDTO.getUser());
    if (user.size() != (storeDTO.getUser() == null ? 0 : storeDTO.getUser().size())) {
      throw new NotFoundException("one of user not found");
    }
    store.setUser(new HashSet<>(user));
  }
}
