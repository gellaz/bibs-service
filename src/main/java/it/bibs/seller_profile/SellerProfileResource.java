package it.bibs.seller_profile;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(value = "/api/seller-profiles", produces = MediaType.APPLICATION_JSON_VALUE)
@SecurityRequirement(name = "oauth2")
@Tag(name = "Seller Profiles", description = "Seller profile and VAT verification management")
@RequiredArgsConstructor
public class SellerProfileResource {

  private final SellerProfileService sellerProfileService;

  @GetMapping
  @Operation(
      summary = "List all seller profiles",
      description =
          "Retrieve all seller profiles. Optionally filter by VAT verification status. Requires ADMIN role.")
  @PreAuthorize("@acl.isAdmin()")
  public ResponseEntity<List<SellerProfileDTO>> getAllSellerProfiles(
      @RequestParam(required = false) final VatVerificationStatus status) {
    if (status != null) {
      return ResponseEntity.ok(sellerProfileService.findAllByStatus(status));
    }
    return ResponseEntity.ok(sellerProfileService.findAll());
  }

  @GetMapping("/{sellerProfileId}")
  @Operation(
      summary = "Get a seller profile by ID",
      description =
          "Retrieve a single seller profile by its unique identifier. Requires ADMIN role.")
  @ApiResponse(responseCode = "200", description = "Seller profile found")
  @ApiResponse(responseCode = "404", description = "Seller profile not found")
  @PreAuthorize("@acl.isAdmin()")
  public ResponseEntity<SellerProfileDTO> getSellerProfile(
      @PathVariable final UUID sellerProfileId) {
    return ResponseEntity.ok(sellerProfileService.get(sellerProfileId));
  }

  @PostMapping
  @Operation(
      summary = "Create a seller profile",
      description =
          "Create a new seller profile. VAT status defaults to PENDING. Requires ADMIN role.")
  @ApiResponse(responseCode = "201", description = "Seller profile created")
  @ApiResponse(responseCode = "400", description = "Validation error")
  @PreAuthorize("@acl.isAdmin()")
  public ResponseEntity<UUID> createSellerProfile(
      @RequestBody @Valid final SellerProfileDTO sellerProfileDTO) {
    final UUID createdId = sellerProfileService.create(sellerProfileDTO);
    return new ResponseEntity<>(createdId, HttpStatus.CREATED);
  }

  @PutMapping("/{sellerProfileId}")
  @Operation(
      summary = "Update a seller profile",
      description = "Update an existing seller profile. Requires ADMIN role.")
  @ApiResponse(responseCode = "200", description = "Seller profile updated")
  @ApiResponse(responseCode = "404", description = "Seller profile not found")
  @ApiResponse(responseCode = "400", description = "Validation error")
  @PreAuthorize("@acl.isAdmin()")
  public ResponseEntity<UUID> updateSellerProfile(
      @PathVariable final UUID sellerProfileId,
      @RequestBody @Valid final SellerProfileDTO sellerProfileDTO) {
    sellerProfileService.update(sellerProfileId, sellerProfileDTO);
    return ResponseEntity.ok(sellerProfileId);
  }

  @DeleteMapping("/{sellerProfileId}")
  @Operation(
      summary = "Delete a seller profile",
      description = "Delete a seller profile by its unique identifier. Requires ADMIN role.")
  @ApiResponse(responseCode = "204", description = "Seller profile deleted")
  @ApiResponse(responseCode = "404", description = "Seller profile not found")
  @PreAuthorize("@acl.isAdmin()")
  public ResponseEntity<Void> deleteSellerProfile(@PathVariable final UUID sellerProfileId) {
    sellerProfileService.delete(sellerProfileId);
    return ResponseEntity.noContent().build();
  }

  @PutMapping("/{sellerProfileId}/verify")
  @Operation(
      summary = "Verify VAT",
      description =
          "Set VAT verification status to VERIFIED. Only ADMIN can perform this action. "
              + "Once verified, the owner can create stores and manage products.")
  @ApiResponse(responseCode = "200", description = "VAT verified successfully")
  @ApiResponse(responseCode = "404", description = "Seller profile not found")
  @ApiResponse(responseCode = "409", description = "VAT is not in PENDING status")
  @PreAuthorize("@acl.isAdmin()")
  public ResponseEntity<Void> verifyVat(@PathVariable final UUID sellerProfileId) {
    sellerProfileService.verifyVat(sellerProfileId);
    return ResponseEntity.ok().build();
  }

  @PutMapping("/{sellerProfileId}/reject")
  @Operation(
      summary = "Reject VAT",
      description =
          "Set VAT verification status to REJECTED. Only ADMIN can perform this action. "
              + "Rejected owners cannot create stores or manage products.")
  @ApiResponse(responseCode = "200", description = "VAT rejected successfully")
  @ApiResponse(responseCode = "404", description = "Seller profile not found")
  @ApiResponse(responseCode = "409", description = "VAT is not in PENDING status")
  @PreAuthorize("@acl.isAdmin()")
  public ResponseEntity<Void> rejectVat(@PathVariable final UUID sellerProfileId) {
    sellerProfileService.rejectVat(sellerProfileId);
    return ResponseEntity.ok().build();
  }
}
