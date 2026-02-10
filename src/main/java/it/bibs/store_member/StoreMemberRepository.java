package it.bibs.store_member;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreMemberRepository extends JpaRepository<StoreMember, UUID> {

  List<StoreMember> findAllByStoreId(UUID storeId);

  List<StoreMember> findAllByUserId(UUID userId);

  boolean existsByStoreIdAndUserId(UUID storeId, UUID userId);

  boolean existsByStoreIdAndUserIdAndRole(UUID storeId, UUID userId, StoreMemberRole role);

  void deleteAllByStoreId(UUID storeId);
}
