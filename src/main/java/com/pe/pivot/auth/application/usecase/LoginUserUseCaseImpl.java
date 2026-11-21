package com.pe.pivot.auth.application.usecase;

import com.pe.pivot.auth.domain.exception.InvalidCredentialsException;
import com.pe.pivot.auth.domain.model.AuthToken;
import com.pe.pivot.auth.domain.port.in.LoginUserUseCase;
import com.pe.pivot.auth.domain.port.out.AuthUserRepositoryPort;
import com.pe.pivot.auth.domain.port.out.JwtTokenPort;
import com.pe.pivot.auth.domain.port.out.PasswordEncoderPort;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Single Responsibility Use Case for authenticating user credentials and issuing a JWT.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LoginUserUseCaseImpl implements LoginUserUseCase {

  private static final String INVALID_CREDENTIALS_MSG = "Invalid username or password.";

  private final AuthUserRepositoryPort userRepository;
  private final PasswordEncoderPort passwordEncoder;
  private final JwtTokenPort jwtTokenPort;

  /**
   * Authenticates a user by validating their username and password,
   * and generates a JWT token upon successful authentication.
   *
   * @param username - the username of the user attempting to log in
   * @param rawPassword - the password of the user attempting to log in
   * @return - a Single emitting an AuthToken if authentication is successful,
   *           or an error if it fails
   */
  @Override
  public Single<AuthToken> login(String username, String rawPassword) {
    String normalizedUsername = username.trim().toLowerCase();

    // Attempt to find the user by username, validate credentials, and generate a JWT token
    return userRepository.findByUsername(normalizedUsername)
            .switchIfEmpty(Single.error(new InvalidCredentialsException(INVALID_CREDENTIALS_MSG)))
            .flatMap(user -> {
              if (!user.isActive()
                      || !passwordEncoder.matches(rawPassword, user.getPasswordHash())) {
                return Single.error(new InvalidCredentialsException(INVALID_CREDENTIALS_MSG));
              }
              return Single.just(jwtTokenPort.generateToken(user));
            })
            .doOnSuccess(token -> log.info("Successful login for username: {}", token.username()));
  }
}