package it.bibs.store;

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

import it.bibs.store_member.StoreMemberDTO;
import it.bibs.store_member.StoreMemberService;

@RestController
@RequestMapping(value = "/api/stores", produces = MediaType.APPLICATION_JSON_VALUE)
@SecurityRequirement(name = "oauth2")
@Tag(name = "Stores", description = "Store management and member endpoints")
@RequiredArgsConstructor
public class StoreResource {

  private final StoreService storeService;
  private final StoreMemberService storeMemberService;

  // ── Store CRUD ────────────────────────────────────────────────────────────

  @GetMapping
  @Operation(summary = "List all stores", description = "Retrieve the complete list of stores.")
  public ResponseEntity<List<StoreDTO>> getAllStores() {
    return ResponseEntity.ok(storeService.findAll());
  }

  @GetMapping("/{storeId}")
  @Operation(
      summary = "Get a store by ID",
      description = "Retrieve a single store by its unique identifier.")
  @ApiResponse(responseCode = "200", description = "Store found")
  @ApiResponse(responseCode = "404", description = "Store not found")
  public ResponseEntity<StoreDTO> getStore(@PathVariable final UUID storeId) {
    return ResponseEntity.ok(storeService.get(storeId));
  }

  @PostMapping
  @Operation(
      summary = "Create a new store",
      description =
          "Create a new store. Requires the owner to have VAT verification status VERIFIED. "
              + "The authenticated user is automatically added as OWNER member.")
  @ApiResponse(responseCode = "201", description = "Store created")
  @ApiResponse(responseCode = "400", description = "Validation error")
  @ApiResponse(responseCode = "401", description = "VAT not verified or unauthorized")
  public ResponseEntity<UUID> createStore(@RequestBody @Valid final StoreDTO storeDTO) {
    final UUID createdId = storeService.create(storeDTO);
    return new ResponseEntity<>(createdId, HttpStatus.CREATED);
  }

  @PutMapping("/{storeId}")
  @Operation(
      summary = "Update a store",
      description = "Update an existing store by its unique identifier.")
  @ApiResponse(responseCode = "200", description = "Store updated")
  @ApiResponse(responseCode = "404", description = "Store not found")
  @ApiResponse(responseCode = "400", description = "Validation error")
  public ResponseEntity<UUID> updateStore(
      @PathVariable final UUID storeId, @RequestBody @Valid final StoreDTO storeDTO) {
    storeService.update(storeId, storeDTO);
    return ResponseEntity.ok(storeId);
  }

  @DeleteMapping("/{storeId}")
  @Operation(summary = "Delete a store", description = "Delete a store by its unique identifier.")
  @ApiResponse(responseCode = "204", description = "Store deleted")
  @ApiResponse(responseCode = "404", description = "Store not found")
  @ApiResponse(responseCode = "409", description = "Store is referenced by other entities")
  public ResponseEntity<Void> deleteStore(@PathVariable final UUID storeId) {
    storeService.delete(storeId);
    return ResponseEntity.noContent().build();
  }

  // ── Store Members ─────────────────────────────────────────────────────────

  @GetMapping("/{storeId}/members")
  @Operation(
      summary = "List store members",
      description = "Retrieve all members (owner, managers, clerks) of a store.")
  @ApiResponse(responseCode = "200", description = "Members list")
  @ApiResponse(responseCode = "404", description = "Store not found")
  public ResponseEntity<List<StoreMemberDTO>> getStoreMembers(@PathVariable final UUID storeId) {
    return ResponseEntity.ok(storeMemberService.findAllByStore(storeId));
  }

  @PostMapping("/{storeId}/members")
  @Operation(
      summary = "Add a store member",
      description =
          "Add a user as a member of the store with the specified role. "
              + "Only the store OWNER can add members.")
  @ApiResponse(responseCode = "201", description = "Member added")
  @ApiResponse(responseCode = "400", description = "Validation error")
  @ApiResponse(responseCode = "404", description = "Store or user not found")
  @ApiResponse(responseCode = "409", description = "User is already a member of this store")
  public ResponseEntity<UUID> addStoreMember(
      @PathVariable final UUID storeId, @RequestBody @Valid final StoreMemberDTO memberDTO) {
    final UUID createdId = storeMemberService.addMember(storeId, memberDTO);
    return new ResponseEntity<>(createdId, HttpStatus.CREATED);
  }

  @DeleteMapping("/{storeId}/members/{memberId}")
  @Operation(
      summary = "Remove a store member",
      description =
          "Remove a member from the store. Only the store OWNER can remove members. "
              + "The OWNER cannot remove themselves.")
  @ApiResponse(responseCode = "204", description = "Member removed")
  @ApiResponse(responseCode = "404", description = "Store or member not found")
  @ApiResponse(responseCode = "409", description = "Cannot remove the store owner")
  public ResponseEntity<Void> removeStoreMember(
      @PathVariable final UUID storeId, @PathVariable final UUID memberId) {
    storeMemberService.removeMember(storeId, memberId);
    return ResponseEntity.noContent().build();
  }
}
