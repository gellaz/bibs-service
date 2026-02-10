package it.bibs.seller_profile;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SellerProfileRepository extends JpaRepository<SellerProfile, UUID> {

  SellerProfile findFirstByUserId(UUID id);

  List<SellerProfile> findAllByVatVerificationStatus(VatVerificationStatus status, Sort sort);

  boolean existsByVatNumberIgnoreCase(String vatNumber);

  boolean existsByUserId(UUID id);
}
