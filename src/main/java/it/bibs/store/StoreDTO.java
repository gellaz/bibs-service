package it.bibs.store;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StoreDTO {

  private UUID id;

  @NotNull private String name;

  private String description;

  private String addressLine1;

  @NotNull private String addressLine2;

  private String city;

  @NotNull private String zipCode;

  @Size(max = 255)
  private String province;

  @NotNull
  @Size(max = 2)
  private String country;

  private Double latitude;

  private Double longitude;
}
