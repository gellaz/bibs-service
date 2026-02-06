package it.bibs.loyalty_account;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LoyaltyAccountRepository extends JpaRepository<LoyaltyAccount, UUID> {

  LoyaltyAccount findFirstByUserId(UUID id);
}
