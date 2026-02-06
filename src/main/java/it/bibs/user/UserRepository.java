package it.bibs.user;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, UUID> {

  User findByIdentitySubject(String identitySubject);

  boolean existsByIdentitySubject(String identitySubject);

  boolean existsByEmail(String email);
}
