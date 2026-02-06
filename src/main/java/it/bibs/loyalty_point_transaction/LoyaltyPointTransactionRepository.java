package it.bibs.loyalty_point_transaction;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LoyaltyPointTransactionRepository
    extends JpaRepository<LoyaltyPointTransaction, UUID> {

  LoyaltyPointTransaction findFirstByUserId(UUID id);
}
