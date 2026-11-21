package com.pe.pivot.auth.application.usecase;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.pe.pivot.auth.application.support.AuthUserDomainInitializer;
import com.pe.pivot.auth.domain.exception.DuplicateUserException;
import com.pe.pivot.auth.domain.model.AuthToken;
import com.pe.pivot.auth.domain.model.AuthUser;
import com.pe.pivot.auth.domain.model.Role;
import com.pe.pivot.auth.domain.port.out.AuthUserRepositoryPort;
import com.pe.pivot.auth.domain.port.out.JwtTokenPort;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.observers.TestObserver;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit test suite for RegisterUserUseCaseImpl.
 */
class RegisterUserUseCaseImplTest {

  private AuthUserRepositoryPort userRepository;
  private AuthUserDomainInitializer initializer;
  private JwtTokenPort tokenPort;
  private RegisterUserUseCaseImpl useCase;
  private AuthUser incomingUser;
  private AuthUser initializedUser;
  private AuthToken issuedToken;

  @BeforeEach
  void setUp() {
    userRepository = mock(AuthUserRepositoryPort.class);
    initializer = mock(AuthUserDomainInitializer.class);
    tokenPort = mock(JwtTokenPort.class);
    useCase = new RegisterUserUseCaseImpl(userRepository, initializer, tokenPort);

    incomingUser = AuthUser.builder()
            .username("  JOHN  ")
            .email("  JOHN@MAIL.COM  ")
            .role(Role.ROLE_USER)
            .customerId("customer-1")
            .build();
    initializedUser = AuthUser.builder()
            .username("john")
            .email("john@mail.com")
            .passwordHash("hashed")
            .role(Role.ROLE_USER)
            .customerId("customer-1")
            .active(true)
            .build();
    issuedToken = AuthToken.builder()
            .accessToken("jwt-token")
            .tokenType("Bearer")
            .username("john")
            .role(Role.ROLE_USER)
            .issuedAt(Instant.now())
            .build();

    when(initializer.normalize(anyString())).thenAnswer(invocation -> {
      String value = invocation.getArgument(0);
      return value.trim().toLowerCase();
    });
  }

  @Test
  @DisplayName("Should be register a new user and issue an initial JWT (happy path)")
  void shouldBeRegisterUserAndIssueToken() {
    // Arrange
    when(userRepository.existsByUsername("john")).thenReturn(Single.just(false));
    when(userRepository.existsByEmail("john@mail.com")).thenReturn(Single.just(false));
    when(initializer.initialize(incomingUser, "secret123")).thenReturn(initializedUser);
    when(userRepository.save(initializedUser)).thenReturn(Single.just(initializedUser));
    when(tokenPort.generateToken(initializedUser)).thenReturn(issuedToken);

    // Act
    TestObserver<AuthToken> observer = useCase.registerUser(incomingUser, "secret123").test();

    // Assert
    observer.assertComplete();
    observer.assertNoErrors();
    observer.assertValue(issuedToken);
    verify(userRepository).save(initializedUser);
    verify(tokenPort).generateToken(initializedUser);
  }

  @Test
  @DisplayName("Should be fail with DuplicateUserException when username already exists")
  void shouldBeFailWhenUsernameAlreadyExists() {
    // Arrange
    when(userRepository.existsByUsername("john")).thenReturn(Single.just(true));
    when(userRepository.existsByEmail("john@mail.com")).thenReturn(Single.just(false));

    // Act
    TestObserver<AuthToken> observer = useCase.registerUser(incomingUser, "secret123").test();

    // Assert
    observer.assertError(DuplicateUserException.class);
    observer.assertError(error -> error instanceof DuplicateUserException
            && "Username is already registered: john".equals(error.getMessage()));
    verify(userRepository, never()).save(any());
  }

  @Test
  @DisplayName("Should be fail with DuplicateUserException when email already exists")
  void shouldBeFailWhenEmailAlreadyExists() {
    // Arrange
    when(userRepository.existsByUsername("john")).thenReturn(Single.just(false));
    when(userRepository.existsByEmail("john@mail.com")).thenReturn(Single.just(true));

    // Act
    TestObserver<AuthToken> observer = useCase.registerUser(incomingUser, "secret123").test();

    // Assert
    observer.assertError(DuplicateUserException.class);
    observer.assertError(error -> error instanceof DuplicateUserException
            && "Email is already registered: john@mail.com".equals(error.getMessage()));
    verify(userRepository, never()).save(any());
  }

  @Test
  @DisplayName("Should be fail with DuplicateUserException when both identifiers already exist")
  void shouldBeFailWhenBothIdentifiersAlreadyExist() {
    // Arrange
    when(userRepository.existsByUsername("john")).thenReturn(Single.just(true));
    when(userRepository.existsByEmail("john@mail.com")).thenReturn(Single.just(true));

    // Act
    TestObserver<AuthToken> observer = useCase.registerUser(incomingUser, "secret123").test();

    // Assert
    observer.assertError(DuplicateUserException.class);
    observer.assertError(error -> error instanceof DuplicateUserException
            && "Username is already registered: john".equals(error.getMessage()));
    verify(initializer, never()).initialize(any(), anyString());
  }

  @Test
  @DisplayName("Should be propagate persistence error when saving the new user fails")
  void shouldBePropagatePersistenceError() {
    // Arrange
    RuntimeException failure = new RuntimeException("mongo unavailable");
    when(userRepository.existsByUsername("john")).thenReturn(Single.just(false));
    when(userRepository.existsByEmail("john@mail.com")).thenReturn(Single.just(false));
    when(initializer.initialize(incomingUser, "secret123")).thenReturn(initializedUser);
    when(userRepository.save(initializedUser)).thenReturn(Single.error(failure));

    // Act
    TestObserver<AuthToken> observer = useCase.registerUser(incomingUser, "secret123").test();

    // Assert
    observer.assertError(failure);
    verify(tokenPort, never()).generateToken(any());
  }

  @Test
  @DisplayName("Should be propagate token generation error after a successful save")
  void shouldBePropagateTokenGenerationError() {
    // Arrange
    RuntimeException failure = new RuntimeException("jwt signing failed");
    when(userRepository.existsByUsername("john")).thenReturn(Single.just(false));
    when(userRepository.existsByEmail("john@mail.com")).thenReturn(Single.just(false));
    when(initializer.initialize(incomingUser, "secret123")).thenReturn(initializedUser);
    when(userRepository.save(initializedUser)).thenReturn(Single.just(initializedUser));
    when(tokenPort.generateToken(initializedUser)).thenThrow(failure);

    // Act
    TestObserver<AuthToken> observer = useCase.registerUser(incomingUser, "secret123").test();

    // Assert
    observer.assertError(failure);
  }
}
