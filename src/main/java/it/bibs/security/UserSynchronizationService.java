package it.bibs.security;

import java.util.Map;
import java.util.Set;

import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import it.bibs.customer_profile.CustomerProfile;
import it.bibs.customer_profile.CustomerProfileRepository;
import it.bibs.user.User;
import it.bibs.user.UserRepository;

/**
 * Synchronize Keycloak users with the database after successful authentication.
 *
 * <p>Handles automatic profile creation based on the Keycloak client:
 *
 * <ul>
 *   <li>{@code bibs-customer} → creates {@link User} + {@link CustomerProfile}
 *   <li>{@code bibs-seller} → creates {@link User} only (seller onboarding requires VAT)
 *   <li>{@code bibs-admin} → creates {@link User} only (admin panel, ADMIN role required)
 *   <li>{@code bibs-swagger} → creates {@link User} only (development client)
 * </ul>
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UserSynchronizationService {

  private static final String CLIENT_CUSTOMER = "bibs-customer";
  private static final String CLIENT_SELLER = "bibs-seller";
  private static final String CLIENT_ADMIN = "bibs-admin";
  private static final String CLIENT_SWAGGER = "bibs-swagger";
  private static final Set<String> KNOWN_CLIENTS =
      Set.of(CLIENT_CUSTOMER, CLIENT_SELLER, CLIENT_ADMIN, CLIENT_SWAGGER);

  private final UserRepository userRepository;
  private final CustomerProfileRepository customerProfileRepository;

  @Transactional
  @EventListener(AuthenticationSuccessEvent.class)
  public void onAuthenticationSuccessEvent(final AuthenticationSuccessEvent event) {
    if (event.getSource() instanceof JwtAuthenticationToken jwtToken) {
      final Object azpObj = jwtToken.getTokenAttributes().get("azp");
      if (azpObj instanceof String azp && KNOWN_CLIENTS.contains(azp)) {
        syncWithDatabase(jwtToken.getTokenAttributes(), azp);
      }
    }
  }

  private void syncWithDatabase(final Map<String, Object> claims, final String clientId) {
    final String subject = claims.get("sub").toString();
    final boolean isNew;

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

    isNew = user.getId() == null;

    if (!isNew) {
      log.debug("Updating existing user after successful authentication: {}", subject);
    }

    user.setEmail(((String) claims.get("email")));
    user.setFirstName(((String) claims.get("given_name")));
    user.setLastName(((String) claims.get("family_name")));
    user = userRepository.save(user);

    if (isNew && CLIENT_CUSTOMER.equals(clientId)) {
      createCustomerProfile(user);
    }
  }

  private void createCustomerProfile(final User user) {
    if (customerProfileRepository.findFirstByUserId(user.getId()) != null) {
      return; // safety guard — should not happen for new users
    }
    log.info("Creating customer profile for new user: {}", user.getIdentitySubject());
    final CustomerProfile profile = new CustomerProfile();
    profile.setUser(user);
    profile.setPointsBalance(0);
    customerProfileRepository.save(profile);
  }
}
