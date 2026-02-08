package it.bibs.security;

import java.util.Objects;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import it.bibs.util.UnauthorizedException;

@Service("acl")
@RequiredArgsConstructor
public class AclService {

  public String getCurrentUserSubject() {
    final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication instanceof JwtAuthenticationToken jwtToken) {
      return jwtToken.getName();
    }
    throw new UnauthorizedException("User not authenticated");
  }

  private boolean hasRole(final String role) {
    final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    return authentication != null
        && authentication.getAuthorities().stream()
            .anyMatch(a -> Objects.equals(a.getAuthority(), role));
  }

  public boolean isAdmin() {
    return hasRole(UserRoles.ADMIN);
  }

  public boolean isUser() {
    return hasRole(UserRoles.USER);
  }
}
