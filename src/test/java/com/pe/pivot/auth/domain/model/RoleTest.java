package com.pe.pivot.auth.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit test suite for Role enum.
 */
class RoleTest {

  @Test
  @DisplayName("Should be expose both supported authorization roles (happy path)")
  void shouldBeExposeSupportedRoles() {
    // Act
    Role[] roles = Role.values();

    // Assert
    assertEquals(2, roles.length);
    assertEquals(Role.ROLE_USER, roles[0]);
    assertEquals(Role.ROLE_ADMIN, roles[1]);
  }

  @Test
  @DisplayName("Should be resolve a role by name and expose its name")
  void shouldBeResolveRoleByName() {
    // Act
    Role role = Role.valueOf("ROLE_ADMIN");

    // Assert
    assertEquals(Role.ROLE_ADMIN, role);
    assertEquals("ROLE_ADMIN", role.name());
  }

  @Test
  @DisplayName("Should be throw IllegalArgumentException for an unknown role name")
  void shouldBeThrowForUnknownRoleName() {
    // Act & Assert
    assertThrows(IllegalArgumentException.class, () -> Role.valueOf("ROLE_UNKNOWN"));
  }
}
