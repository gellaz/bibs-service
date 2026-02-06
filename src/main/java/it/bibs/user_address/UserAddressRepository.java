package it.bibs.user_address;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAddressRepository extends JpaRepository<UserAddress, UUID> {

  UserAddress findFirstByUserId(UUID id);
}
