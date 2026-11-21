package com.pe.pivot.auth.infrastructure.adapter.out.security;

import com.pe.pivot.auth.domain.exception.InvalidTokenException;
import com.pe.pivot.auth.domain.model.AuthToken;
import com.pe.pivot.auth.domain.model.AuthUser;
import com.pe.pivot.auth.domain.model.Role;
import com.pe.pivot.auth.domain.model.TokenClaims;
import com.pe.pivot.auth.domain.port.out.JwtTokenPort;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.time.Instant;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;



/**
 * Security Adapter implementing JwtTokenPort using JJWT (HS256) with Vault-managed secrets.
 */
@Component
public class JjwtTokenAdapter implements JwtTokenPort {

  private static final String TOKEN_TYPE_BEARER = "Bearer";
  private static final String CLAIM_ROLE = "role";
  private static final String CLAIM_CUSTOMER_ID = "customerId";

  private final SecretKey signingKey;
  private final long expirationMs;

  /**
   * Constructor for JjwtTokenAdapter, initializing the signing key and token
   * expiration time from application properties.
   *
   * @param secretKey - the Base64-encoded secret key for signing JWTs
   * @param expirationMs - the expiration time for JWTs in milliseconds
   *        (default: 3600000 ms)
   */
  public JjwtTokenAdapter(
          @Value("${application.security.jwt.secret-key}") String secretKey,
          @Value("${application.security.jwt.expiration-ms:3600000}") long expirationMs) {
    this.signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
    this.expirationMs = expirationMs;
  }

  /**
   * Generates a JWT token for the given authenticated user, embedding their
   * username, role, and customer ID as claims.
   *
   * @param user - the authenticated user for whom the token is to be generated
   * @return - an AuthToken object containing the generated JWT and associated metadata
   */
  @Override
  public AuthToken generateToken(AuthUser user) {
    Instant now = Instant.now();
    Instant expiry = now.plusMillis(expirationMs);

    // Build the JWT token with claims for username, role, customer ID, issued at, and expiration
    String jwt = Jwts.builder()
            .subject(user.getUsername())
            .claim(CLAIM_ROLE, user.getRole().name())
            .claim(CLAIM_CUSTOMER_ID, user.getCustomerId())
            .claim(Claims.ISSUED_AT, now.getEpochSecond())
            .claim(Claims.EXPIRATION, expiry.getEpochSecond())
            .signWith(signingKey)
            .compact();

    // Return the AuthToken object with the generated JWT and associated metadata
    return AuthToken.builder()
            .accessToken(jwt)
            .tokenType(TOKEN_TYPE_BEARER)
            .expiresInMs(expirationMs)
            .username(user.getUsername())
            .role(user.getRole())
            .issuedAt(now)
            .build();
  }

  /**
   * Validates the provided JWT token and extracts its claims, returning a TokenClaims object.
   *
   * @param token - the JWT token to be validated and parsed
   * @return - a TokenClaims object containing the extracted claims if the token is valid
   * @throws InvalidTokenException - if the token is invalid or expired
   * @throws IllegalArgumentException - if the token is null or empty
   * @throws JwtException - if the token cannot be parsed or verified
   */
  @Override
  public TokenClaims validateAndParse(String token) {
    try {
      // Parse the JWT token and extract claims, verifying the signature with the signing key
      Claims claims = Jwts.parser()
              .verifyWith(signingKey)
              .build()
              .parseSignedClaims(token)
              .getPayload();

      // Extract the role claim and convert it to a Role enum,
      // defaulting to ROLE_USER if not present
      String roleValue = claims.get(CLAIM_ROLE, String.class);
      Role role = roleValue != null ? Role.valueOf(roleValue) : Role.ROLE_USER;

      // Return a TokenClaims object with the extracted claims and validity status
      return TokenClaims.builder()
              .valid(true)
              .username(claims.getSubject())
              .role(role)
              .customerId(claims.get(CLAIM_CUSTOMER_ID, String.class))
              .expiresAt(claims.getExpiration().toInstant())
              .build();
    } catch (JwtException | IllegalArgumentException ex) {
      throw new InvalidTokenException("Invalid or expired JWT token.");
    }
  }
}
