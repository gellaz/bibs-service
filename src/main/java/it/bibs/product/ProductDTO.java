package it.bibs.product;

import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductDTO {

  private UUID id;

  @NotNull private String title;

  private String description;

  @NotNull
  @JsonProperty("isActive")
  private Boolean isActive;

  private List<UUID> productCategories;

  @NotNull @ProductProductStockUnique private UUID productStock;
}
