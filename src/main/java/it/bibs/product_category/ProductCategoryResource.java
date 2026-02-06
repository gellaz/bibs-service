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

import io.swagger.v3.oas.annotations.responses.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(value = "/api/product-categories", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ProductCategoryResource {

  private final ProductCategoryService productCategoryService;

  @GetMapping
  public ResponseEntity<List<ProductCategoryDTO>> getAllProductCategories() {
    return ResponseEntity.ok(productCategoryService.findAll());
  }

  @GetMapping("/{productCategoryId}")
  public ResponseEntity<ProductCategoryDTO> getProductCategory(
      @PathVariable final UUID productCategoryId) {
    return ResponseEntity.ok(productCategoryService.get(productCategoryId));
  }

  @PostMapping
  @ApiResponse(responseCode = "201")
  public ResponseEntity<UUID> createProductCategory(
      @RequestBody @Valid final ProductCategoryDTO productCategoryDTO) {
    final UUID createdId = productCategoryService.create(productCategoryDTO);
    return new ResponseEntity<>(createdId, HttpStatus.CREATED);
  }

  @PutMapping("/{productCategoryId}")
  public ResponseEntity<UUID> updateProductCategory(
      @PathVariable final UUID productCategoryId,
      @RequestBody @Valid final ProductCategoryDTO productCategoryDTO) {
    productCategoryService.update(productCategoryId, productCategoryDTO);
    return ResponseEntity.ok(productCategoryId);
  }

  @DeleteMapping("/{productCategoryId}")
  @ApiResponse(responseCode = "204")
  public ResponseEntity<Void> deleteProductCategory(@PathVariable final UUID productCategoryId) {
    productCategoryService.delete(productCategoryId);
    return ResponseEntity.noContent().build();
  }
}
