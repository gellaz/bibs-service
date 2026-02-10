package it.bibs.user;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import it.bibs.business_profile.BusinessProfile;
import it.bibs.business_profile.BusinessProfileDTO;
import it.bibs.loyalty_account.LoyaltyAccount;
import it.bibs.loyalty_account.LoyaltyAccountDTO;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

  UserDTO toDTO(User user);

  @Mapping(source = "user.id", target = "user")
  BusinessProfileDTO toBusinessProfileDTO(BusinessProfile businessProfile);

  LoyaltyAccountDTO toLoyaltyAccountDTO(LoyaltyAccount loyaltyAccount);

  @Mapping(target = "id", ignore = true)
  User updateUser(UserDTO userDTO, @MappingTarget User user);
}
