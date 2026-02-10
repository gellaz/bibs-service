package it.bibs.store_member;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StoreMemberDTO {

  private UUID id;

  @NotNull private UUID userId;

  @NotNull private StoreMemberRole role;
}
