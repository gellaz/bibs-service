package it.bibs.user;

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
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(value = "/api/users", produces = MediaType.APPLICATION_JSON_VALUE)
@SecurityRequirement(name = "oauth2")
@Tag(name = "Users", description = "User management and self-service onboarding endpoints")
@RequiredArgsConstructor
public class UserResource {

  private final UserService userService;

  // ── Self-service ──────────────────────────────────────────────────────────

  @GetMapping("/me")
  @Operation(
      summary = "Get current user",
      description =
          "Retrieve the profile of the currently authenticated user, "
              + "including seller profile and customer profile if present.")
  public ResponseEntity<UserDTO> getMe() {
    return ResponseEntity.ok(userService.getMe());
  }

  @PostMapping("/me/seller-profile")
  @Operation(
      summary = "Onboard as seller",
      description =
          "Register the current user as a seller by providing a VAT number. "
              + "Creates a SellerProfile with PENDING verification status. "
              + "An ADMIN must verify the VAT before the user can create stores or manage products.")
  @ApiResponse(responseCode = "201", description = "Seller profile created — VAT pending review")
  @ApiResponse(responseCode = "400", description = "Validation error (e.g. invalid VAT format)")
  @ApiResponse(
      responseCode = "409",
      description = "User already has a seller profile or VAT number already in use")
  public ResponseEntity<Void> onboardAsSeller(
      @RequestBody @Valid final SellerOnboardingRequest request) {
    userService.onboardAsSeller(request.getVatNumber());
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @PostMapping("/me/customer-profile")
  @Operation(
      summary = "Onboard as customer",
      description =
          "Register the current user as a customer. "
              + "Creates a CustomerProfile with zero loyalty points balance.")
  @ApiResponse(responseCode = "201", description = "Customer profile created")
  @ApiResponse(responseCode = "409", description = "User already has a customer profile")
  public ResponseEntity<Void> onboardAsCustomer() {
    userService.onboardAsCustomer();
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  // ── CRUD ──────────────────────────────────────────────────────────────────

  @GetMapping
  @Operation(summary = "List all users", description = "Retrieve the complete list of users.")
  public ResponseEntity<List<UserDTO>> getAllUsers() {
    return ResponseEntity.ok(userService.findAll());
  }

  @GetMapping("/{userId}")
  @Operation(
      summary = "Get a user by ID",
      description = "Retrieve a single user by its unique identifier. Requires ADMIN role.")
  @ApiResponse(responseCode = "200", description = "User found")
  @ApiResponse(responseCode = "404", description = "User not found")
  @ApiResponse(responseCode = "403", description = "Forbidden — requires ADMIN role")
  @PreAuthorize("@acl.isAdmin()")
  public ResponseEntity<UserDTO> getUser(@PathVariable final UUID userId) {
    return ResponseEntity.ok(userService.get(userId));
  }

  @PostMapping
  @Operation(summary = "Create a new user", description = "Create a new user account.")
  @ApiResponse(responseCode = "201", description = "User created")
  @ApiResponse(responseCode = "400", description = "Validation error")
  public ResponseEntity<UUID> createUser(@RequestBody @Valid final UserDTO userDTO) {
    final UUID createdId = userService.create(userDTO);
    return new ResponseEntity<>(createdId, HttpStatus.CREATED);
  }

  @PutMapping("/{userId}")
  @Operation(
      summary = "Update a user",
      description = "Update an existing user by its unique identifier.")
  @ApiResponse(responseCode = "200", description = "User updated")
  @ApiResponse(responseCode = "404", description = "User not found")
  @ApiResponse(responseCode = "400", description = "Validation error")
  public ResponseEntity<UUID> updateUser(
      @PathVariable final UUID userId, @RequestBody @Valid final UserDTO userDTO) {
    userService.update(userId, userDTO);
    return ResponseEntity.ok(userId);
  }

  @DeleteMapping("/{userId}")
  @Operation(summary = "Delete a user", description = "Delete a user by its unique identifier.")
  @ApiResponse(responseCode = "204", description = "User deleted")
  @ApiResponse(responseCode = "404", description = "User not found")
  @ApiResponse(responseCode = "409", description = "User is referenced by other entities")
  public ResponseEntity<Void> deleteUser(@PathVariable final UUID userId) {
    userService.delete(userId);
    return ResponseEntity.noContent().build();
  }
}
