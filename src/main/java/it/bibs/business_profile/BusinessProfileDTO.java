package it.bibs.business_profile;

import java.time.OffsetDateTime;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BusinessProfileDTO {

  private UUID id;

  @NotNull
  @Size(max = 11)
  @BusinessProfileVatNumberUnique
  private String vatNumber;

  @NotNull private VatVerificationStatus vatVerificationStatus;

  private OffsetDateTime vatVerifiedAt;

  @NotNull @BusinessProfileUserUnique private UUID user;
}
