package it.bibs.business_profile;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BusinessProfileRepository extends JpaRepository<BusinessProfile, UUID> {

  BusinessProfile findFirstByUserId(UUID id);
}
