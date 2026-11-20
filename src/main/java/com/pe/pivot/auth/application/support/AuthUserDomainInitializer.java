package com.pe.pivot.auth.application.support;

import com.pe.pivot.auth.domain.model.AuthUser;
import com.pe.pivot.auth.domain.model.Role;
import com.pe.pivot.auth.domain.port.out.PasswordEncoderPort;
import java.time.Instant;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;



/**
 * Domain Initializer normalizing credentials, hashing passwords via PasswordEncoderPort,
 * and applying default role and lifecycle fields for new AuthUser entities (SRP).
 */
@Component
@RequiredArgsConstructor
public class AuthUserDomainInitializer {

  private final PasswordEncoderPort passwordEncoder;

  /**
   * Normalizes a username or email identifier consistently.
   *
   * @param value the raw string value
   * @return trimmed lowercase string
   */
  public String normalize(String value) {
    return value.trim().toLowerCase();
  }

  /**
   * Initializes a new AuthUser with normalized identity fields, BCrypt password hash,
   * default role, active status, and audit timestamps.
   *
   * @param incoming the raw AuthUser domain input
   * @param rawPassword the plain-text password to hash
   * @return fully initialized AuthUser ready for persistence
   */
  public AuthUser initialize(AuthUser incoming, String rawPassword) {
    Instant now = Instant.now();
    return AuthUser.builder()
            .username(normalize(incoming.getUsername()))
            .email(normalize(incoming.getEmail()))
            .passwordHash(passwordEncoder.encode(rawPassword))
            .role(Optional.ofNullable(incoming.getRole()).orElse(Role.ROLE_USER))
            .customerId(incoming.getCustomerId())
            .active(true)
            .createdAt(now)
            .updatedAt(now)
            .build();
  }
}
