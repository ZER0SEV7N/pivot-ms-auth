package com.pe.pivot.auth.application.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.pe.pivot.auth.domain.model.AuthUser;
import com.pe.pivot.auth.domain.model.Role;
import com.pe.pivot.auth.domain.port.out.PasswordEncoderPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit test suite for AuthUserDomainInitializer.
 */
class AuthUserDomainInitializerTest {

  private PasswordEncoderPort passwordEncoder;
  private AuthUserDomainInitializer initializer;

  @BeforeEach
  void setUp() {
    passwordEncoder = mock(PasswordEncoderPort.class);
    initializer = new AuthUserDomainInitializer(passwordEncoder);
  }

  @Test
  @DisplayName("Should be normalize identifiers by trimming and lower-casing them")
  void shouldBeNormalizeIdentifiers() {
    // Act & Assert
    assertEquals("john.doe", initializer.normalize("  John.Doe  "));
  }

  @Test
  @DisplayName("Should be initialize a user with hashed password and provided role (happy path)")
  void shouldBeInitializeUserWithProvidedRole() {
    // Arrange
    AuthUser incoming = AuthUser.builder()
            .username("  JOHN  ")
            .email("  JOHN@MAIL.COM  ")
            .role(Role.ROLE_ADMIN)
            .customerId("customer-1")
            .build();
    when(passwordEncoder.encode("secret123")).thenReturn("bcrypt-hash");

    // Act
    AuthUser result = initializer.initialize(incoming, "secret123");

    // Assert
    assertEquals("john", result.getUsername());
    assertEquals("john@mail.com", result.getEmail());
    assertEquals("bcrypt-hash", result.getPasswordHash());
    assertEquals(Role.ROLE_ADMIN, result.getRole());
    assertEquals("customer-1", result.getCustomerId());
    assertTrue(result.isActive());
    assertNotNull(result.getCreatedAt());
    assertNotNull(result.getUpdatedAt());
    verify(passwordEncoder).encode("secret123");
  }

  @Test
  @DisplayName("Should be assign ROLE_USER when the incoming role is null")
  void shouldBeAssignDefaultRoleWhenRoleIsNull() {
    // Arrange
    AuthUser incoming = AuthUser.builder()
            .username("john")
            .email("john@mail.com")
            .build();
    when(passwordEncoder.encode("secret123")).thenReturn("bcrypt-hash");

    // Act
    AuthUser result = initializer.initialize(incoming, "secret123");

    // Assert
    assertEquals(Role.ROLE_USER, result.getRole());
    assertTrue(result.isActive());
    assertFalse(result.getUsername().isBlank());
  }
}
