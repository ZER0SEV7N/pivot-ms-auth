package com.pe.pivot.auth.infrastructure.adapter.out.security;

import com.pe.pivot.auth.domain.port.out.PasswordEncoderPort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Password encoder adapter implementing BCrypt hashing.
 */
@Component
public class BcryptPasswordEncoderAdapter implements PasswordEncoderPort {

  private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

  /**
   * Encodes the provided raw password using BCrypt hashing algorithm.
   *
   * @param rawPassword - the raw password to be encoded
   * @return - the BCrypt hashed password
   */
  @Override
  public String encode(String rawPassword) {
    return encoder.encode(rawPassword);
  }

  /**
   * Verifies if the provided raw password matches the encoded (hashed) password.
   *
   * @param rawPassword - the raw password to be verified
   * @param encodedPassword - the encoded (hashed) password to compare against
   * @return - true if the raw password matches the encoded password, false otherwise
   */
  @Override
  public boolean matches(String rawPassword, String encodedPassword) {
    return encoder.matches(rawPassword, encodedPassword);
  }
}
