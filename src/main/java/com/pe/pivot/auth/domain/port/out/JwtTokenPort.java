package com.pe.pivot.auth.domain.port.out;

import com.pe.pivot.auth.domain.model.AuthToken;
import com.pe.pivot.auth.domain.model.AuthUser;
import com.pe.pivot.auth.domain.model.TokenClaims;

/**
 * Output port abstracting JWT token generation and cryptographic verification.
 */
public interface JwtTokenPort {

  /**
   * Generates a JWT token for the given authenticated user.
   * The token encapsulates the user's identity and claims, allowing secure communication between services.
   *
   * @param user - the authenticated user for whom the token is to be generated
   * @return - an AuthToken representing the generated JWT token
   */
  AuthToken generateToken(AuthUser user);

  /**
   * Validates the provided JWT token and parses its claims.
   *
   * @param token - the JWT token to be validated and parsed
   * @return - a TokenClaims object containing the claims extracted from the validated token
   */
  TokenClaims validateAndParse(String token);
}