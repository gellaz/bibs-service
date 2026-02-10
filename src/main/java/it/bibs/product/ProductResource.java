package it.bibs.product;

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
@RequestMapping(value = "/api/products", produces = MediaType.APPLICATION_JSON_VALUE)
@SecurityRequirement(name = "oauth2")
@Tag(name = "Products", description = "Product management endpoints")
@RequiredArgsConstructor
public class ProductResource {

  private final ProductService productService;

  @GetMapping
  @Operation(summary = "List all products", description = "Retrieve the complete list of products.")
  public ResponseEntity<List<ProductDTO>> getAllProducts() {
    return ResponseEntity.ok(productService.findAll());
  }

  @GetMapping("/{productId}")
  @Operation(
      summary = "Get a product by ID",
      description = "Retrieve a single product by its unique identifier.")
  @ApiResponse(responseCode = "200", description = "Product found")
  @ApiResponse(responseCode = "404", description = "Product not found")
  public ResponseEntity<ProductDTO> getProduct(@PathVariable final UUID productId) {
    return ResponseEntity.ok(productService.get(productId));
  }

  @PostMapping
  @Operation(
      summary = "Create a new product",
      description = "Create a new product. Requires a verified store owner or store employee role.")
  @ApiResponse(responseCode = "201", description = "Product created")
  @ApiResponse(responseCode = "400", description = "Validation error")
  public ResponseEntity<UUID> createProduct(@RequestBody @Valid final ProductDTO productDTO) {
    final UUID createdId = productService.create(productDTO);
    return new ResponseEntity<>(createdId, HttpStatus.CREATED);
  }

  @PutMapping("/{productId}")
  @Operation(
      summary = "Update a product",
      description = "Update an existing product by its unique identifier.")
  @ApiResponse(responseCode = "200", description = "Product updated")
  @ApiResponse(responseCode = "404", description = "Product not found")
  @ApiResponse(responseCode = "400", description = "Validation error")
  public ResponseEntity<UUID> updateProduct(
      @PathVariable final UUID productId, @RequestBody @Valid final ProductDTO productDTO) {
    productService.update(productId, productDTO);
    return ResponseEntity.ok(productId);
  }

  @DeleteMapping("/{productId}")
  @Operation(
      summary = "Delete a product",
      description = "Delete a product by its unique identifier.")
  @ApiResponse(responseCode = "204", description = "Product deleted")
  @ApiResponse(responseCode = "404", description = "Product not found")
  @ApiResponse(responseCode = "409", description = "Product is referenced by other entities")
  public ResponseEntity<Void> deleteProduct(@PathVariable final UUID productId) {
    productService.delete(productId);
    return ResponseEntity.noContent().build();
  }
}
