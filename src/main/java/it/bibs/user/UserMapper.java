package it.bibs.user;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

  UserDTO updateUserDTO(User user, @MappingTarget UserDTO userDTO);

  @Mapping(target = "id", ignore = true)
  User updateUser(UserDTO userDTO, @MappingTarget User user);
}
