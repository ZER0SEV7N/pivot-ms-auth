package com.pe.pivot.auth;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Smoke test for MsAuthApplication class definition.
 */
class MsAuthApplicationTests {

  @Test
  @DisplayName("Should be instantiate MsAuthApplication successfully")
  void shouldBeInstantiateApplicationSuccessfully() {
    // Arrange & Act
    MsAuthApplication application = new MsAuthApplication();

    // Assert
    assertNotNull(application);
  }
}
