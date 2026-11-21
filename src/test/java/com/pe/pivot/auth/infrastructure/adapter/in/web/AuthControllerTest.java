package com.pe.pivot.auth.infrastructure.adapter.in.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.pe.pivot.auth.domain.exception.DuplicateUserException;
import com.pe.pivot.auth.domain.exception.InvalidCredentialsException;
import com.pe.pivot.auth.domain.exception.InvalidTokenException;
import com.pe.pivot.auth.domain.model.AuthToken;
import com.pe.pivot.auth.domain.model.AuthUser;
import com.pe.pivot.auth.domain.model.Role;
import com.pe.pivot.auth.domain.model.TokenClaims;
import com.pe.pivot.auth.domain.port.in.LoginUserUseCase;
import com.pe.pivot.auth.domain.port.in.RegisterUserUseCase;
import com.pe.pivot.auth.domain.port.in.ValidateTokenUseCase;
import com.pe.pivot.auth.infrastructure.adapter.in.web.dto.AuthResponse;
import com.pe.pivot.auth.infrastructure.adapter.in.web.dto.LoginRequest;
import com.pe.pivot.auth.infrastructure.adapter.in.web.dto.RegisterRequest;
import com.pe.pivot.auth.infrastructure.adapter.in.web.dto.TokenValidationResponse;
import com.pe.pivot.auth.infrastructure.adapter.in.web.mapper.AuthWebMapper;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.observers.TestObserver;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit test suite for AuthController.
 */
class AuthControllerTest {

  private RegisterUserUseCase registerUserUseCase;
  private LoginUserUseCase loginUserUseCase;
  private ValidateTokenUseCase validateTokenUseCase;
  private AuthWebMapper mapper;
  private AuthController controller;
  private AuthToken token;
  private TokenClaims claims;
  private Instant now;

  @BeforeEach
  void setUp() {
    registerUserUseCase = mock(RegisterUserUseCase.class);
    loginUserUseCase = mock(LoginUserUseCase.class);
    validateTokenUseCase = mock(ValidateTokenUseCase.class);
    mapper = mock(AuthWebMapper.class);
    controller = new AuthController(
            registerUserUseCase, loginUserUseCase, validateTokenUseCase, mapper);
    now = Instant.parse("2026-01-01T00:00:00Z");
    token = AuthToken.builder()
            .accessToken("jwt-token")
            .tokenType("Bearer")
            .expiresInMs(3_600_000L)
            .username("john")
            .role(Role.ROLE_USER)
            .issuedAt(now)
            .build();
    claims = TokenClaims.builder()
            .valid(true)
            .username("john")
            .role(Role.ROLE_USER)
            .customerId("customer-1")
            .expiresAt(now.plusSeconds(60))
            .build();
  }

  @Test
  @DisplayName("Should be register a user and return an AuthResponse (happy path)")
  void shouldBeRegisterUserAndReturnAuthResponse() {
    // Arrange
    RegisterRequest request = RegisterRequest.builder()
            .username("john")
            .email("john@mail.com")
            .password("secret123")
            .role(Role.ROLE_USER)
            .build();
    AuthUser domain = AuthUser.builder().username("john").build();
    AuthResponse expected = AuthResponse.builder()
            .accessToken("jwt-token")
            .tokenType("Bearer")
            .username("john")
            .role(Role.ROLE_USER)
            .issuedAt(now)
            .build();
    when(mapper.toDomain(request)).thenReturn(domain);
    when(registerUserUseCase.registerUser(domain, "secret123")).thenReturn(Single.just(token));
    when(mapper.toAuthResponse(token)).thenReturn(expected);

    // Act
    TestObserver<AuthResponse> observer = controller.register(request).test();

    // Assert
    observer.assertComplete();
    observer.assertNoErrors();
    observer.assertValue(expected);
    verify(registerUserUseCase).registerUser(domain, "secret123");
    verify(mapper).toAuthResponse(token);
  }

  @Test
  @DisplayName("Should be login a user and return an AuthResponse (happy path)")
  void shouldBeLoginUserAndReturnAuthResponse() {
    // Arrange
    LoginRequest request = LoginRequest.builder()
            .username("john")
            .password("secret123")
            .build();
    AuthResponse expected = AuthResponse.builder()
            .accessToken("jwt-token")
            .tokenType("Bearer")
            .username("john")
            .role(Role.ROLE_USER)
            .issuedAt(now)
            .build();
    when(loginUserUseCase.login("john", "secret123")).thenReturn(Single.just(token));
    when(mapper.toAuthResponse(token)).thenReturn(expected);

    // Act
    TestObserver<AuthResponse> observer = controller.login(request).test();

    // Assert
    observer.assertComplete();
    observer.assertNoErrors();
    observer.assertValue(expected);
    verify(loginUserUseCase).login("john", "secret123");
  }

  @Test
  @DisplayName("Should be validate the Authorization header and return the token claims "
          + "(happy path)")
  void shouldBeValidateTokenAndReturnClaims() {
    // Arrange
    TokenValidationResponse expected = TokenValidationResponse.builder()
            .valid(true)
            .username("john")
            .role(Role.ROLE_USER)
            .expiresAt(now.plusSeconds(60))
            .build();
    when(validateTokenUseCase.validate("Bearer jwt-token")).thenReturn(Single.just(claims));
    when(mapper.toValidationResponse(claims)).thenReturn(expected);

    // Act
    TestObserver<TokenValidationResponse> observer =
            controller.validate("Bearer jwt-token").test();

    // Assert
    observer.assertComplete();
    observer.assertNoErrors();
    observer.assertValue(expected);
    verify(validateTokenUseCase).validate("Bearer jwt-token");
  }

  @Test
  @DisplayName("Should be propagate DuplicateUserException when registration is rejected")
  void shouldBePropagateRegistrationError() {
    // Arrange
    RegisterRequest request = RegisterRequest.builder()
            .username("john")
            .email("john@mail.com")
            .password("secret123")
            .build();
    AuthUser domain = AuthUser.builder().username("john").build();
    DuplicateUserException failure = new DuplicateUserException("Username exists");
    when(mapper.toDomain(any(RegisterRequest.class))).thenReturn(domain);
    when(registerUserUseCase.registerUser(domain, "secret123"))
            .thenReturn(Single.error(failure));

    // Act
    TestObserver<AuthResponse> observer = controller.register(request).test();

    // Assert
    observer.assertError(DuplicateUserException.class);
    observer.assertError(failure);
    observer.assertNoValues();
    verify(mapper, never()).toAuthResponse(any());
  }

  @Test
  @DisplayName("Should be propagate InvalidCredentialsException when login is rejected")
  void shouldBePropagateLoginError() {
    // Arrange
    LoginRequest request = LoginRequest.builder()
            .username("john")
            .password("wrong")
            .build();
    InvalidCredentialsException failure =
            new InvalidCredentialsException("Invalid username or password.");
    when(loginUserUseCase.login("john", "wrong")).thenReturn(Single.error(failure));

    // Act
    TestObserver<AuthResponse> observer = controller.login(request).test();

    // Assert
    observer.assertError(InvalidCredentialsException.class);
    observer.assertError(error -> error instanceof InvalidCredentialsException
            && "Invalid username or password.".equals(error.getMessage()));
  }

  @Test
  @DisplayName("Should be propagate InvalidTokenException when the token cannot be validated")
  void shouldBePropagateValidationError() {
    // Arrange
    InvalidTokenException failure =
            new InvalidTokenException("Invalid or expired JWT token.");
    when(validateTokenUseCase.validate(anyString())).thenReturn(Single.error(failure));

    // Act
    TestObserver<TokenValidationResponse> observer =
            controller.validate("Bearer broken").test();

    // Assert
    observer.assertError(InvalidTokenException.class);
    observer.assertNoValues();
  }
}
