package it.bibs.user;

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
@RequestMapping(value = "/api/users", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class UserResource {

  private final UserService userService;

  @GetMapping
  public ResponseEntity<List<UserDTO>> getAllUsers() {
    return ResponseEntity.ok(userService.findAll());
  }

  @GetMapping("/{userId}")
  public ResponseEntity<UserDTO> getUser(@PathVariable final UUID userId) {
    return ResponseEntity.ok(userService.get(userId));
  }

  @PostMapping
  @ApiResponse(responseCode = "201")
  public ResponseEntity<UUID> createUser(@RequestBody @Valid final UserDTO userDTO) {
    final UUID createdId = userService.create(userDTO);
    return new ResponseEntity<>(createdId, HttpStatus.CREATED);
  }

  @PutMapping("/{userId}")
  public ResponseEntity<UUID> updateUser(
      @PathVariable final UUID userId, @RequestBody @Valid final UserDTO userDTO) {
    userService.update(userId, userDTO);
    return ResponseEntity.ok(userId);
  }

  @DeleteMapping("/{userId}")
  @ApiResponse(responseCode = "204")
  public ResponseEntity<Void> deleteUser(@PathVariable final UUID userId) {
    userService.delete(userId);
    return ResponseEntity.noContent().build();
  }
}
