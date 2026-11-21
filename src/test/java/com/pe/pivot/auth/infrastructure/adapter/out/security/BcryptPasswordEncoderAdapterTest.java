package com.pe.pivot.auth.infrastructure.adapter.out.security;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit test suite for BcryptPasswordEncoderAdapter.
 */
class BcryptPasswordEncoderAdapterTest {

  private BcryptPasswordEncoderAdapter adapter;

  @BeforeEach
  void setUp() {
    adapter = new BcryptPasswordEncoderAdapter();
  }

  @Test
  @DisplayName("Should be encode a password and match it afterwards (happy path)")
  void shouldBeEncodeAndMatchPassword() {
    // Arrange
    String rawPassword = "Pivot2026!";

    // Act
    String encoded = adapter.encode(rawPassword);

    // Assert
    assertNotEquals(rawPassword, encoded);
    assertTrue(adapter.matches(rawPassword, encoded));
  }

  @Test
  @DisplayName("Should be reject a non-matching raw password")
  void shouldBeRejectNonMatchingPassword() {
    // Arrange
    String encoded = adapter.encode("Pivot2026!");

    // Act & Assert
    assertFalse(adapter.matches("WrongPassword", encoded));
  }
}
