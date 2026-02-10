package it.bibs.seller_profile;

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
public interface SellerProfileMapper {

  @Mapping(target = "user", ignore = true)
  SellerProfileDTO updateSellerProfileDTO(
      SellerProfile sellerProfile, @MappingTarget SellerProfileDTO sellerProfileDTO);

  @AfterMapping
  default void afterUpdateSellerProfileDTO(
      SellerProfile sellerProfile, @MappingTarget SellerProfileDTO sellerProfileDTO) {
    sellerProfileDTO.setUser(
        sellerProfile.getUser() == null ? null : sellerProfile.getUser().getId());
  }

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "user", ignore = true)
  SellerProfile updateSellerProfile(
      SellerProfileDTO sellerProfileDTO,
      @MappingTarget SellerProfile sellerProfile,
      @Context UserRepository userRepository);

  @AfterMapping
  default void afterUpdateSellerProfile(
      SellerProfileDTO sellerProfileDTO,
      @MappingTarget SellerProfile sellerProfile,
      @Context UserRepository userRepository) {
    final User user =
        sellerProfileDTO.getUser() == null
            ? null
            : userRepository
                .findById(sellerProfileDTO.getUser())
                .orElseThrow(() -> new NotFoundException("user not found"));
    sellerProfile.setUser(user);
  }
}
