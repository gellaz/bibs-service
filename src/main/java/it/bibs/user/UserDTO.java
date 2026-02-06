package it.bibs.user;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {

  private UUID id;

  @NotNull @UserIdentitySubjectUnique private String identitySubject;

  @NotNull @UserEmailUnique private String email;

  private String firstName;

  private String lastName;
}
