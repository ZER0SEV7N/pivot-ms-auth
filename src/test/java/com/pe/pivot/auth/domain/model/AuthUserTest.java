package com.pe.pivot.auth.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit test suite for AuthUser domain model.
 */
class AuthUserTest {

  private static final Instant CREATED_AT = Instant.parse("2026-01-01T00:00:00Z");
  private static final Instant UPDATED_AT = Instant.parse("2026-02-01T00:00:00Z");

  @Test
  @DisplayName("Should be build an AuthUser with all fields (happy path)")
  void shouldBeBuildAuthUserWithAllFields() {
    // Arrange & Act
    AuthUser user = AuthUser.builder()
            .id("user-1")
            .username("john")
            .email("john@mail.com")
            .passwordHash("hash")
            .role(Role.ROLE_ADMIN)
            .customerId("customer-1")
            .active(true)
            .createdAt(CREATED_AT)
            .updatedAt(UPDATED_AT)
            .build();

    // Assert
    assertEquals("user-1", user.getId());
    assertEquals("john", user.getUsername());
    assertEquals("john@mail.com", user.getEmail());
    assertEquals("hash", user.getPasswordHash());
    assertEquals(Role.ROLE_ADMIN, user.getRole());
    assertEquals("customer-1", user.getCustomerId());
    assertTrue(user.isActive());
    assertEquals(CREATED_AT, user.getCreatedAt());
    assertEquals(UPDATED_AT, user.getUpdatedAt());
  }

  @Test
  @DisplayName("Should be set and read every field through the no-args constructor")
  void shouldBeSetAndGetEveryField() {
    // Arrange
    AuthUser user = new AuthUser();

    // Act
    user.setId("user-2");
    user.setUsername("jane");
    user.setEmail("jane@mail.com");
    user.setPasswordHash("other-hash");
    user.setRole(Role.ROLE_USER);
    user.setCustomerId("customer-2");
    user.setActive(false);
    user.setCreatedAt(CREATED_AT);
    user.setUpdatedAt(UPDATED_AT);

    // Assert
    assertEquals("user-2", user.getId());
    assertEquals("jane", user.getUsername());
    assertEquals("jane@mail.com", user.getEmail());
    assertEquals("other-hash", user.getPasswordHash());
    assertEquals(Role.ROLE_USER, user.getRole());
    assertEquals("customer-2", user.getCustomerId());
    assertFalse(user.isActive());
    assertEquals(CREATED_AT, user.getCreatedAt());
    assertEquals(UPDATED_AT, user.getUpdatedAt());
  }

  @Test
  @DisplayName("Should be create an AuthUser through the all-args constructor")
  void shouldBeCreateAuthUserWithAllArgsConstructor() {
    // Arrange & Act
    AuthUser user = new AuthUser("user-3", "bob", "bob@mail.com", "hash",
            Role.ROLE_USER, "customer-3", true, CREATED_AT, UPDATED_AT);

    // Assert
    assertEquals("user-3", user.getId());
    assertEquals("bob", user.getUsername());
    assertEquals(Role.ROLE_USER, user.getRole());
    assertTrue(user.isActive());
  }
}
