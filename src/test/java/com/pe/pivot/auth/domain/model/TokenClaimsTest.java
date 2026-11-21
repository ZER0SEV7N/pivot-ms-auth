package com.pe.pivot.auth.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit test suite for TokenClaims domain record.
 */
class TokenClaimsTest {

  private static final Instant EXPIRES_AT = Instant.parse("2026-01-01T01:00:00Z");

  @Test
  @DisplayName("Should be build TokenClaims and read all its components (happy path)")
  void shouldBeBuildTokenClaimsAndReadComponents() {
    // Arrange & Act
    TokenClaims claims = TokenClaims.builder()
            .valid(true)
            .username("john")
            .role(Role.ROLE_ADMIN)
            .customerId("customer-1")
            .expiresAt(EXPIRES_AT)
            .build();

    // Assert
    assertTrue(claims.valid());
    assertEquals("john", claims.username());
    assertEquals(Role.ROLE_ADMIN, claims.role());
    assertEquals("customer-1", claims.customerId());
    assertEquals(EXPIRES_AT, claims.expiresAt());
  }

  @Test
  @DisplayName("Should be compare TokenClaims records by value")
  void shouldBeCompareTokenClaimsRecordsByValue() {
    // Arrange
    TokenClaims first = new TokenClaims(true, "john", Role.ROLE_USER, "c-1", EXPIRES_AT);
    TokenClaims second = new TokenClaims(true, "john", Role.ROLE_USER, "c-1", EXPIRES_AT);
    TokenClaims different = new TokenClaims(false, "john", Role.ROLE_USER, "c-1", EXPIRES_AT);

    // Act & Assert
    assertEquals(first, second);
    assertEquals(first.hashCode(), second.hashCode());
    assertNotEquals(first, different);
    assertNotNull(first.toString());
  }
}
