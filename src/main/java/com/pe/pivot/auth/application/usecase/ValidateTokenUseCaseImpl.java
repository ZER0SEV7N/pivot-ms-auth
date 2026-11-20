package com.pe.pivot.auth.application.usecase;

import com.pe.pivot.auth.domain.exception.InvalidTokenException;
import com.pe.pivot.auth.domain.model.TokenClaims;
import com.pe.pivot.auth.domain.port.in.ValidateTokenUseCase;
import com.pe.pivot.auth.domain.port.out.JwtTokenPort;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Single Responsibility Use Case for validating a Bearer JWT header and extracting its claims.
 */
@Service
@RequiredArgsConstructor
public class ValidateTokenUseCaseImpl implements ValidateTokenUseCase {

  // Constants
  private static final String BEARER_PREFIX = "Bearer ";

  private final JwtTokenPort tokenPort;

  /**
   * Validates the provided authorization header, ensuring it contains a valid Bearer token.
   *
   * @param authorizationHeader - the authorization header containing the token to be validated
   * @return - a Single emitting the TokenClaims if the token is valid, or an error if invalid
   */
  @Override
  public Single<TokenClaims> validate(String authorizationHeader) {
    if ( authorizationHeader == null || !authorizationHeader.startsWith(BEARER_PREFIX) ) {
      return Single.error(
              new InvalidTokenException("Missing or malformed Authorization Bearer header."));
    }

    // Extract the token from the header and validate it using the JwtTokenPort
    String token = authorizationHeader.substring(BEARER_PREFIX.length()).trim();
    return Single.fromCallable(() -> tokenPort.validateAndParse(token));
  }
}
