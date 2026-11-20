package com.pe.pivot.auth.domain.port.in;

import com.pe.pivot.auth.domain.model.TokenClaims;
import io.reactivex.rxjava3.core.Single;

/**
 * Use case interface for validating authorization tokens.
 * Implementations of this interface are responsible for verifying the validity of tokens and extracting claims from them.
 */
public interface ValidateTokenUseCase {

  /**
   * Validates the provided authorization header and returns a Single emitting the TokenClaims if the token is valid.
   *
   * @param authorizationHeader - the authorization header containing the token to be validated
   * @return - a Single emitting the TokenClaims if the token is valid, or an error if the token is invalid
   */
  Single<TokenClaims> validate(String authorizationHeader);
}
