package it.bibs.security;

import java.util.Map;
import java.util.Set;

import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import it.bibs.user.User;
import it.bibs.user.UserRepository;

/** Synchronize Keycloak users with the database after successful authentication. */
@Service
@Slf4j
@RequiredArgsConstructor
public class UserSynchronizationService {

  private final UserRepository userRepository;

  private void syncWithDatabase(final Map<String, Object> claims) {
    final String subject = claims.get("sub").toString();
    User user =
        userRepository
            .findByIdentitySubject(subject)
            .orElseGet(
                () -> {
                  log.info("Adding new user after successful authentication: {}", subject);
                  User newUser = new User();
                  newUser.setIdentitySubject(subject);
                  return newUser;
                });

    if (user.getId() != null) {
      log.debug("Updating existing user after successful authentication: {}", subject);
    }
    user.setEmail(((String) claims.get("email")));
    user.setFirstName(((String) claims.get("given_name")));
    user.setLastName(((String) claims.get("family_name")));
    userRepository.save(user);
  }

  @EventListener(AuthenticationSuccessEvent.class)
  public void onAuthenticationSuccessEvent(final AuthenticationSuccessEvent event) {
    if (event.getSource() instanceof JwtAuthenticationToken jwtToken) {
      final Object azpObj = jwtToken.getTokenAttributes().get("azp");
      if (azpObj instanceof String azp && Set.of("bibs-web", "bibs-swagger").contains(azp)) {
        syncWithDatabase(jwtToken.getTokenAttributes());
      }
    }
  }
}
