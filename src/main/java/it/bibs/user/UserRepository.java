package it.bibs.user;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, UUID> {

  Optional<User> findByIdentitySubject(String identitySubject);

  @EntityGraph(attributePaths = {"customerProfile", "sellerProfile"})
  @Query("SELECT u FROM User u WHERE u.identitySubject = :identitySubject")
  Optional<User> findByIdentitySubjectWithProfiles(
      @Param("identitySubject") String identitySubject);

  boolean existsByIdentitySubject(String identitySubject);

  boolean existsByEmail(String email);
}
