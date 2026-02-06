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

import io.swagger.v3.oas.annotations.responses.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(value = "/api/stores", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class StoreResource {

  private final StoreService storeService;

  @GetMapping
  public ResponseEntity<List<StoreDTO>> getAllStores() {
    return ResponseEntity.ok(storeService.findAll());
  }

  @GetMapping("/{storeId}")
  public ResponseEntity<StoreDTO> getStore(@PathVariable final UUID storeId) {
    return ResponseEntity.ok(storeService.get(storeId));
  }

  @PostMapping
  @ApiResponse(responseCode = "201")
  public ResponseEntity<UUID> createStore(@RequestBody @Valid final StoreDTO storeDTO) {
    final UUID createdId = storeService.create(storeDTO);
    return new ResponseEntity<>(createdId, HttpStatus.CREATED);
  }

  @PutMapping("/{storeId}")
  public ResponseEntity<UUID> updateStore(
      @PathVariable final UUID storeId, @RequestBody @Valid final StoreDTO storeDTO) {
    storeService.update(storeId, storeDTO);
    return ResponseEntity.ok(storeId);
  }

  @DeleteMapping("/{storeId}")
  @ApiResponse(responseCode = "204")
  public ResponseEntity<Void> deleteStore(@PathVariable final UUID storeId) {
    storeService.delete(storeId);
    return ResponseEntity.noContent().build();
  }
}
