package it.bibs.customer_profile;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerProfileRepository extends JpaRepository<CustomerProfile, UUID> {

  CustomerProfile findFirstByUserId(UUID id);
}
