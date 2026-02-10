package it.bibs.user;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/** Request body for seller onboarding. Only requires a VAT number. */
@Getter
@Setter
public class SellerOnboardingRequest {

  @NotNull
  @Size(min = 11, max = 11, message = "VAT number must be exactly 11 characters")
  private String vatNumber;
}
