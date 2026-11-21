package com.pe.pivot.auth.infrastructure.adapter.out.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.pe.pivot.auth.domain.exception.InvalidTokenException;
import com.pe.pivot.auth.domain.model.AuthToken;
import com.pe.pivot.auth.domain.model.AuthUser;
import com.pe.pivot.auth.domain.model.Role;
import com.pe.pivot.auth.domain.model.TokenClaims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.time.Instant;
import java.util.Date;
import javax.crypto.SecretKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit test suite for JjwtTokenAdapter.
 */
class JjwtTokenAdapterTest {

  private static final String SECRET =
          "MDEyMzQ1Njc4OWFiY2RlZjAxMjM0NTY3ODlhYmNkZWY=";
  private static final long EXPIRATION_MS = 3_600_000L;

  private JjwtTokenAdapter adapter;
  private SecretKey signingKey;

  @BeforeEach
  void setUp() {
    adapter = new JjwtTokenAdapter(SECRET, EXPIRATION_MS);
    signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET));
  }

  @Test
  @DisplayName("Should be generate and validate a JWT containing the user claims (happy path)")
  void shouldBeGenerateAndValidateToken() {
    // Arrange
    AuthUser user = AuthUser.builder()
            .username("alice")
            .role(Role.ROLE_ADMIN)
            .customerId("customer-9")
            .build();

    // Act
    AuthToken token = adapter.generateToken(user);
    final TokenClaims claims = adapter.validateAndParse(token.accessToken());

    // Assert
    assertNotNull(token.accessToken());
    assertEquals("Bearer", token.tokenType());
    assertEquals(EXPIRATION_MS, token.expiresInMs());
    assertEquals("alice", token.username());
    assertEquals(Role.ROLE_ADMIN, token.role());
    assertNotNull(token.issuedAt());
    assertTrue(claims.valid());
    assertEquals("alice", claims.username());
    assertEquals(Role.ROLE_ADMIN, claims.role());
    assertEquals("customer-9", claims.customerId());
    assertNotNull(claims.expiresAt());
  }

  @Test
  @DisplayName("Should be default to ROLE_USER when the role claim is absent")
  void shouldBeDefaultRoleWhenRoleClaimIsAbsent() {
    // Arrange
    Instant now = Instant.now();
    String jwt = Jwts.builder()
            .subject("bob")
            .claim("customerId", "customer-3")
            .issuedAt(Date.from(now))
            .expiration(Date.from(now.plusSeconds(60)))
            .signWith(signingKey)
            .compact();

    // Act
    TokenClaims claims = adapter.validateAndParse(jwt);

    // Assert
    assertEquals(Role.ROLE_USER, claims.role());
    assertEquals("bob", claims.username());
    assertEquals("customer-3", claims.customerId());
    assertTrue(claims.valid());
  }

  @Test
  @DisplayName("Should be throw InvalidTokenException when the token is malformed")
  void shouldBeThrowWhenTokenIsMalformed() {
    // Act & Assert
    assertThrows(InvalidTokenException.class, () -> adapter.validateAndParse("not-a-jwt"));
  }

  @Test
  @DisplayName("Should be throw InvalidTokenException when the token is null")
  void shouldBeThrowWhenTokenIsNull() {
    // Act & Assert
    assertThrows(InvalidTokenException.class, () -> adapter.validateAndParse(null));
  }

  @Test
  @DisplayName("Should be throw InvalidTokenException when the token is expired")
  void shouldBeThrowWhenTokenIsExpired() {
    // Arrange
    JjwtTokenAdapter expiredAdapter = new JjwtTokenAdapter(SECRET, -1_000L);
    AuthUser user = AuthUser.builder()
            .username("alice")
            .role(Role.ROLE_USER)
            .build();
    AuthToken token = expiredAdapter.generateToken(user);

    // Act & Assert
    assertThrows(InvalidTokenException.class,
            () -> expiredAdapter.validateAndParse(token.accessToken()));
  }
}
