package com.pe.pivot.auth.application.usecase;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.pe.pivot.auth.domain.exception.InvalidTokenException;
import com.pe.pivot.auth.domain.model.Role;
import com.pe.pivot.auth.domain.model.TokenClaims;
import com.pe.pivot.auth.domain.port.out.JwtTokenPort;
import io.reactivex.rxjava3.observers.TestObserver;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit test suite for ValidateTokenUseCaseImpl.
 */
class ValidateTokenUseCaseImplTest {

  private JwtTokenPort tokenPort;
  private ValidateTokenUseCaseImpl useCase;

  @BeforeEach
  void setUp() {
    tokenPort = mock(JwtTokenPort.class);
    useCase = new ValidateTokenUseCaseImpl(tokenPort);
  }

  @Test
  @DisplayName("Should be validate a well-formed Bearer token and return its claims (happy path)")
  void shouldBeValidateBearerTokenAndReturnClaims() {
    // Arrange
    TokenClaims claims = TokenClaims.builder()
            .valid(true)
            .username("john")
            .role(Role.ROLE_USER)
            .customerId("customer-1")
            .expiresAt(Instant.now().plusSeconds(60))
            .build();
    when(tokenPort.validateAndParse("jwt-value")).thenReturn(claims);

    // Act
    TestObserver<TokenClaims> observer =
            useCase.validate("Bearer   jwt-value   ").test();

    // Assert
    observer.assertComplete();
    observer.assertNoErrors();
    observer.assertValue(claims);
    verify(tokenPort).validateAndParse("jwt-value");
  }

  @Test
  @DisplayName("Should be fail with InvalidTokenException when header is null")
  void shouldBeFailWhenHeaderIsNull() {
    // Act
    TestObserver<TokenClaims> observer = useCase.validate(null).test();

    // Assert
    observer.assertError(InvalidTokenException.class);
    observer.assertError(error -> error instanceof InvalidTokenException
            && "Missing or malformed Authorization Bearer header."
            .equals(error.getMessage()));
    verify(tokenPort, never()).validateAndParse(null);
  }

  @Test
  @DisplayName("Should be fail with InvalidTokenException when Bearer prefix is missing")
  void shouldBeFailWhenBearerPrefixIsMissing() {
    // Act
    TestObserver<TokenClaims> observer = useCase.validate("Basic credentials").test();

    // Assert
    observer.assertError(InvalidTokenException.class);
    verify(tokenPort, never()).validateAndParse("Basic credentials");
  }

  @Test
  @DisplayName("Should be propagate parser error when token is invalid or expired")
  void shouldBePropagateParserError() {
    // Arrange
    InvalidTokenException failure = new InvalidTokenException("Invalid or expired JWT token.");
    when(tokenPort.validateAndParse("broken-token")).thenThrow(failure);

    // Act
    TestObserver<TokenClaims> observer = useCase.validate("Bearer broken-token").test();

    // Assert
    observer.assertError(InvalidTokenException.class);
    observer.assertError(failure);
  }
}
