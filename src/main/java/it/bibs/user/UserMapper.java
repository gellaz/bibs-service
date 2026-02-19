package it.bibs.user;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import it.bibs.customer_profile.CustomerProfile;
import it.bibs.customer_profile.CustomerProfileDTO;
import it.bibs.seller_profile.SellerProfile;
import it.bibs.seller_profile.SellerProfileDTO;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

  UserDTO toDTO(User user);

  @Mapping(target = "user", ignore = true)
  SellerProfileDTO toSellerProfileDTO(SellerProfile sellerProfile);

  @AfterMapping
  default void afterToSellerProfileDTO(
      SellerProfile sellerProfile, @MappingTarget SellerProfileDTO sellerProfileDTO) {
    sellerProfileDTO.setUser(
        sellerProfile.getUser() == null ? null : sellerProfile.getUser().getId());
  }

  CustomerProfileDTO toCustomerProfileDTO(CustomerProfile customerProfile);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "sellerProfile", ignore = true)
  @Mapping(target = "customerProfile", ignore = true)
  User updateUser(UserDTO userDTO, @MappingTarget User user);
}
