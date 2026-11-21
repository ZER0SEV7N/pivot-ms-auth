package com.pe.pivot.auth.application.usecase;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.pe.pivot.auth.domain.exception.InvalidCredentialsException;
import com.pe.pivot.auth.domain.model.AuthToken;
import com.pe.pivot.auth.domain.model.AuthUser;
import com.pe.pivot.auth.domain.model.Role;
import com.pe.pivot.auth.domain.port.out.AuthUserRepositoryPort;
import com.pe.pivot.auth.domain.port.out.JwtTokenPort;
import com.pe.pivot.auth.domain.port.out.PasswordEncoderPort;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.observers.TestObserver;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit test suite for LoginUserUseCaseImpl.
 */
class LoginUserUseCaseImplTest {

  private AuthUserRepositoryPort userRepository;
  private PasswordEncoderPort passwordEncoder;
  private JwtTokenPort jwtTokenPort;
  private LoginUserUseCaseImpl useCase;

  @BeforeEach
  void setUp() {
    userRepository = mock(AuthUserRepositoryPort.class);
    passwordEncoder = mock(PasswordEncoderPort.class);
    jwtTokenPort = mock(JwtTokenPort.class);
    useCase = new LoginUserUseCaseImpl(userRepository, passwordEncoder, jwtTokenPort);
  }

  @Test
  @DisplayName("Should be login successfully and return a generated JWT token (happy path)")
  void shouldBeLoginSuccessfullyAndReturnGeneratedToken() {
    // Arrange
    AuthUser user = AuthUser.builder()
            .username("john")
            .passwordHash("hashed-password")
            .active(true)
            .build();
    AuthToken token = AuthToken.builder()
            .accessToken("jwt-token")
            .tokenType("Bearer")
            .expiresInMs(3600000L)
            .username("john")
            .role(Role.ROLE_USER)
            .issuedAt(Instant.now())
            .build();
    when(userRepository.findByUsername("john")).thenReturn(Maybe.just(user));
    when(passwordEncoder.matches("secret", "hashed-password")).thenReturn(true);
    when(jwtTokenPort.generateToken(user)).thenReturn(token);

    // Act
    TestObserver<AuthToken> observer = useCase.login("  JOHN  ", "secret").test();

    // Assert
    observer.assertComplete();
    observer.assertNoErrors();
    observer.assertValue(token);
    verify(userRepository).findByUsername("john");
    verify(passwordEncoder).matches("secret", "hashed-password");
    verify(jwtTokenPort).generateToken(user);
  }

  @Test
  @DisplayName("Should be fail with InvalidCredentialsException when user does not exist")
  void shouldBeFailWhenUserDoesNotExist() {
    // Arrange
    when(userRepository.findByUsername("ghost")).thenReturn(Maybe.empty());

    // Act
    TestObserver<AuthToken> observer = useCase.login("ghost", "secret").test();

    // Assert
    observer.assertError(InvalidCredentialsException.class);
    observer.assertError(error -> error instanceof InvalidCredentialsException
            && "Invalid username or password.".equals(error.getMessage()));
    verify(passwordEncoder, never()).matches(any(), any());
    verify(jwtTokenPort, never()).generateToken(any());
  }

  @Test
  @DisplayName("Should be fail with InvalidCredentialsException when user is inactive")
  void shouldBeFailWhenUserIsInactive() {
    // Arrange
    AuthUser inactiveUser = AuthUser.builder()
            .username("john")
            .passwordHash("hashed-password")
            .active(false)
            .build();
    when(userRepository.findByUsername("john")).thenReturn(Maybe.just(inactiveUser));

    // Act
    TestObserver<AuthToken> observer = useCase.login("john", "secret").test();

    // Assert
    observer.assertError(InvalidCredentialsException.class);
    verify(passwordEncoder, never()).matches(any(), any());
    verify(jwtTokenPort, never()).generateToken(any());
  }

  @Test
  @DisplayName("Should be fail with InvalidCredentialsException when password does not match")
  void shouldBeFailWhenPasswordDoesNotMatch() {
    // Arrange
    AuthUser user = AuthUser.builder()
            .username("john")
            .passwordHash("hashed-password")
            .active(true)
            .build();
    when(userRepository.findByUsername("john")).thenReturn(Maybe.just(user));
    when(passwordEncoder.matches("wrong", "hashed-password")).thenReturn(false);

    // Act
    TestObserver<AuthToken> observer = useCase.login("john", "wrong").test();

    // Assert
    observer.assertError(InvalidCredentialsException.class);
    verify(jwtTokenPort, never()).generateToken(any());
  }

  @Test
  @DisplayName("Should be propagate repository error without generating a token")
  void shouldBePropagateRepositoryError() {
    // Arrange
    RuntimeException failure = new RuntimeException("database unavailable");
    when(userRepository.findByUsername("john")).thenReturn(Maybe.error(failure));

    // Act
    TestObserver<AuthToken> observer = useCase.login("john", "secret").test();

    // Assert
    observer.assertError(RuntimeException.class);
    observer.assertError(failure);
    verify(jwtTokenPort, never()).generateToken(any());
  }
}
