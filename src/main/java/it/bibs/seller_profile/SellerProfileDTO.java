package it.bibs.seller_profile;

import java.time.OffsetDateTime;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SellerProfileDTO {

  private UUID id;

  @NotNull
  @Size(max = 11)
  @SellerProfileVatNumberUnique
  private String vatNumber;

  @NotNull private VatVerificationStatus vatVerificationStatus;

  private OffsetDateTime vatVerifiedAt;

  @NotNull @SellerProfileUserUnique private UUID user;
}
