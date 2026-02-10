package it.bibs.user;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import it.bibs.customer_profile.CustomerProfileDTO;
import it.bibs.seller_profile.SellerProfileDTO;

@Getter
@Setter
public class UserDTO {

  private UUID id;

  @NotNull @UserIdentitySubjectUnique private String identitySubject;

  @NotNull @UserEmailUnique private String email;

  private String firstName;

  private String lastName;

  private SellerProfileDTO sellerProfile;

  private CustomerProfileDTO customerProfile;
}
