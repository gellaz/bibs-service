package it.bibs.business_profile;

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
public interface BusinessProfileMapper {

  @Mapping(target = "user", ignore = true)
  BusinessProfileDTO updateBusinessProfileDTO(
      BusinessProfile businessProfile, @MappingTarget BusinessProfileDTO businessProfileDTO);

  @AfterMapping
  default void afterUpdateBusinessProfileDTO(
      BusinessProfile businessProfile, @MappingTarget BusinessProfileDTO businessProfileDTO) {
    businessProfileDTO.setUser(
        businessProfile.getUser() == null ? null : businessProfile.getUser().getId());
  }

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "user", ignore = true)
  BusinessProfile updateBusinessProfile(
      BusinessProfileDTO businessProfileDTO,
      @MappingTarget BusinessProfile businessProfile,
      @Context UserRepository userRepository);

  @AfterMapping
  default void afterUpdateBusinessProfile(
      BusinessProfileDTO businessProfileDTO,
      @MappingTarget BusinessProfile businessProfile,
      @Context UserRepository userRepository) {
    final User user =
        businessProfileDTO.getUser() == null
            ? null
            : userRepository
                .findById(businessProfileDTO.getUser())
                .orElseThrow(() -> new NotFoundException("user not found"));
    businessProfile.setUser(user);
  }
}
