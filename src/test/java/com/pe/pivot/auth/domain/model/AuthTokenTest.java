package com.pe.pivot.auth.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit test suite for AuthToken domain record.
 */
class AuthTokenTest {

  private static final Instant ISSUED_AT = Instant.parse("2026-01-01T00:00:00Z");

  @Test
  @DisplayName("Should be build an AuthToken and read all its components (happy path)")
  void shouldBeBuildAuthTokenAndReadComponents() {
    // Arrange & Act
    AuthToken token = AuthToken.builder()
            .accessToken("jwt-token")
            .tokenType("Bearer")
            .expiresInMs(3_600_000L)
            .username("john")
            .role(Role.ROLE_USER)
            .issuedAt(ISSUED_AT)
            .build();

    // Assert
    assertEquals("jwt-token", token.accessToken());
    assertEquals("Bearer", token.tokenType());
    assertEquals(3_600_000L, token.expiresInMs());
    assertEquals("john", token.username());
    assertEquals(Role.ROLE_USER, token.role());
    assertEquals(ISSUED_AT, token.issuedAt());
  }

  @Test
  @DisplayName("Should be compare AuthToken records by value")
  void shouldBeCompareAuthTokenRecordsByValue() {
    // Arrange
    AuthToken first = new AuthToken("jwt", "Bearer", 1000L, "john",
            Role.ROLE_USER, ISSUED_AT);
    AuthToken second = new AuthToken("jwt", "Bearer", 1000L, "john",
            Role.ROLE_USER, ISSUED_AT);
    AuthToken different = new AuthToken("other", "Bearer", 1000L, "john",
            Role.ROLE_USER, ISSUED_AT);

    // Act & Assert
    assertEquals(first, second);
    assertEquals(first.hashCode(), second.hashCode());
    assertNotEquals(first, different);
    assertNotNull(first.toString());
  }
}
