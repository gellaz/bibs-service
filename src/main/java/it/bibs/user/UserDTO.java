package it.bibs.user;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import it.bibs.business_profile.BusinessProfileDTO;
import it.bibs.loyalty_account.LoyaltyAccountDTO;

@Getter
@Setter
public class UserDTO {

  private UUID id;

  @NotNull @UserIdentitySubjectUnique private String identitySubject;

  @NotNull @UserEmailUnique private String email;

  private String firstName;

  private String lastName;

  private BusinessProfileDTO businessProfile;

  private LoyaltyAccountDTO loyaltyAccount;
}
