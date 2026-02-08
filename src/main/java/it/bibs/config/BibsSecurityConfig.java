package it.bibs.config;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class BibsSecurityConfig {

  /** Read claims from attribute realm_access.roles as SimpleGrantedAuthority. */
  private List<GrantedAuthority> mapAuthorities(final Map<String, Object> attributes) {
    @SuppressWarnings("unchecked")
    final Map<String, Object> realmAccess =
        ((Map<String, Object>) attributes.getOrDefault("realm_access", Collections.emptyMap()));
    @SuppressWarnings("unchecked")
    final Collection<String> roles =
        ((Collection<String>) realmAccess.getOrDefault("roles", List.of()));
    return roles.stream()
        .map(role -> ((GrantedAuthority) new SimpleGrantedAuthority(role)))
        .toList();
  }

  @Bean
  public JwtAuthenticationConverter jwtAuthenticationConverter() {
    final JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
    converter.setJwtGrantedAuthoritiesConverter(source -> mapAuthorities(source.getClaims()));
    return converter;
  }

  @Bean
  public SecurityFilterChain bibsSecurityFilterChain(final HttpSecurity http) {
    return http.cors(withDefaults())
        .csrf(AbstractHttpConfigurer::disable)
        .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
        .authorizeHttpRequests(
            authorize ->
                authorize
                    .requestMatchers("/actuator/**", "/swagger-ui/**", "/api-docs/**")
                    .permitAll()
                    .anyRequest()
                    .authenticated())
        .oauth2ResourceServer(oauth2 -> oauth2.jwt(withDefaults()))
        .build();
  }
}
