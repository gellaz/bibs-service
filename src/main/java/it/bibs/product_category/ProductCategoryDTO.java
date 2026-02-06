package it.bibs.product_category;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductCategoryDTO {

  private UUID id;

  @NotNull
  @Size(max = 255)
  @ProductCategoryNameUnique
  private String name;
}
