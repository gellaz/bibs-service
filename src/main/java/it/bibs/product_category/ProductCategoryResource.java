package it.bibs.product_category;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(value = "/api/product-categories", produces = MediaType.APPLICATION_JSON_VALUE)
@SecurityRequirement(name = "oauth2")
@Tag(name = "Product Categories", description = "Product category management endpoints")
@RequiredArgsConstructor
public class ProductCategoryResource {

  private final ProductCategoryService productCategoryService;

  @GetMapping
  @Operation(
      summary = "List all product categories",
      description = "Retrieve the complete list of product categories.")
  public ResponseEntity<List<ProductCategoryDTO>> getAllProductCategories() {
    return ResponseEntity.ok(productCategoryService.findAll());
  }

  @GetMapping("/{productCategoryId}")
  @Operation(
      summary = "Get a product category by ID",
      description = "Retrieve a single product category by its unique identifier.")
  @ApiResponse(responseCode = "200", description = "Product category found")
  @ApiResponse(responseCode = "404", description = "Product category not found")
  public ResponseEntity<ProductCategoryDTO> getProductCategory(
      @PathVariable final UUID productCategoryId) {
    return ResponseEntity.ok(productCategoryService.get(productCategoryId));
  }

  @PostMapping
  @Operation(
      summary = "Create a new product category",
      description = "Create a new product category. Category names must be unique.")
  @ApiResponse(responseCode = "201", description = "Product category created")
  @ApiResponse(responseCode = "400", description = "Validation error")
  public ResponseEntity<UUID> createProductCategory(
      @RequestBody @Valid final ProductCategoryDTO productCategoryDTO) {
    final UUID createdId = productCategoryService.create(productCategoryDTO);
    return new ResponseEntity<>(createdId, HttpStatus.CREATED);
  }

  @PutMapping("/{productCategoryId}")
  @Operation(
      summary = "Update a product category",
      description = "Update an existing product category by its unique identifier.")
  @ApiResponse(responseCode = "200", description = "Product category updated")
  @ApiResponse(responseCode = "404", description = "Product category not found")
  @ApiResponse(responseCode = "400", description = "Validation error")
  public ResponseEntity<UUID> updateProductCategory(
      @PathVariable final UUID productCategoryId,
      @RequestBody @Valid final ProductCategoryDTO productCategoryDTO) {
    productCategoryService.update(productCategoryId, productCategoryDTO);
    return ResponseEntity.ok(productCategoryId);
  }

  @DeleteMapping("/{productCategoryId}")
  @Operation(
      summary = "Delete a product category",
      description = "Delete a product category by its unique identifier.")
  @ApiResponse(responseCode = "204", description = "Product category deleted")
  @ApiResponse(responseCode = "404", description = "Product category not found")
  @ApiResponse(responseCode = "409", description = "Product category is referenced by products")
  public ResponseEntity<Void> deleteProductCategory(@PathVariable final UUID productCategoryId) {
    productCategoryService.delete(productCategoryId);
    return ResponseEntity.noContent().build();
  }
}
