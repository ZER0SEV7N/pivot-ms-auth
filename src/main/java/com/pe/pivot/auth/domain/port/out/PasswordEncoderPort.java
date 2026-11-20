package com.pe.pivot.auth.domain.port.out;

/**
 * Output port abstracting password hashing and verification.
 */
public interface PasswordEncoderPort {

  /**
   * Encodes the provided raw password into a secure hashed format.
   *
   * @param rawPassword - the raw password to be encoded
   * @return - the encoded (hashed) password
   */
  String encode(String rawPassword);

  /**
   * Checks if the provided raw password matches the encoded password.
   *
   * @param rawPassword - the raw password to be verified
   * @param encodedPassword - the encoded (hashed) password to compare against
   * @return - true if the raw password matches the encoded password, false otherwise
   */
  boolean matches(String rawPassword, String encodedPassword);
}