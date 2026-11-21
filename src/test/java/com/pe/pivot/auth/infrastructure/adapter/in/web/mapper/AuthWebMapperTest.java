package com.pe.pivot.auth.infrastructure.adapter.in.web.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.pe.pivot.auth.domain.model.AuthToken;
import com.pe.pivot.auth.domain.model.AuthUser;
import com.pe.pivot.auth.domain.model.Role;
import com.pe.pivot.auth.domain.model.TokenClaims;
import com.pe.pivot.auth.infrastructure.adapter.in.web.dto.AuthResponse;
import com.pe.pivot.auth.infrastructure.adapter.in.web.dto.RegisterRequest;
import com.pe.pivot.auth.infrastructure.adapter.in.web.dto.TokenValidationResponse;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit test suite for AuthWebMapper.
 */
class AuthWebMapperTest {

  private AuthWebMapper mapper;
  private Instant now;

  @BeforeEach
  void setUp() {
    mapper = new AuthWebMapper();
    now = Instant.now();
  }

  @Test
  @DisplayName("Should be map a RegisterRequest to an AuthUser (happy path)")
  void shouldBeMapRegisterRequestToDomain() {
    // Arrange
    RegisterRequest request = RegisterRequest.builder()
            .username("john")
            .email("john@mail.com")
            .password("secret123")
            .role(Role.ROLE_USER)
            .customerId("customer-1")
            .build();

    // Act
    AuthUser result = mapper.toDomain(request);

    // Assert
    assertEquals("john", result.getUsername());
    assertEquals("john@mail.com", result.getEmail());
    assertEquals(Role.ROLE_USER, result.getRole());
    assertEquals("customer-1", result.getCustomerId());
  }

  @Test
  @DisplayName("Should be map an AuthToken to an AuthResponse")
  void shouldBeMapTokenToResponse() {
    // Arrange
    AuthToken token = AuthToken.builder()
            .accessToken("jwt-token")
            .tokenType("Bearer")
            .expiresInMs(1000L)
            .username("john")
            .role(Role.ROLE_ADMIN)
            .issuedAt(now)
            .build();

    // Act
    AuthResponse response = mapper.toAuthResponse(token);

    // Assert
    assertEquals("jwt-token", response.accessToken());
    assertEquals("Bearer", response.tokenType());
    assertEquals(1000L, response.expiresInMs());
    assertEquals("john", response.username());
    assertEquals(Role.ROLE_ADMIN, response.role());
    assertEquals(now, response.issuedAt());
  }

  @Test
  @DisplayName("Should be map TokenClaims to a TokenValidationResponse")
  void shouldBeMapClaimsToValidationResponse() {
    // Arrange
    TokenClaims claims = TokenClaims.builder()
            .valid(true)
            .username("john")
            .role(Role.ROLE_USER)
            .customerId("customer-1")
            .expiresAt(now)
            .build();

    // Act
    TokenValidationResponse response = mapper.toValidationResponse(claims);

    // Assert
    assertTrue(response.valid());
    assertFalse(response.username().isBlank());
    assertEquals("john", response.username());
    assertEquals(Role.ROLE_USER, response.role());
    assertEquals(now, response.expiresAt());
  }
}
