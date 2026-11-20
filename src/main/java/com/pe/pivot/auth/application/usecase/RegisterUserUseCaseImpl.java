package com.pe.pivot.auth.application.usecase;

import com.pe.pivot.auth.application.support.AuthUserDomainInitializer;
import com.pe.pivot.auth.domain.exception.DuplicateUserException;
import com.pe.pivot.auth.domain.model.AuthToken;
import com.pe.pivot.auth.domain.model.AuthUser;
import com.pe.pivot.auth.domain.port.in.RegisterUserUseCase;
import com.pe.pivot.auth.domain.port.out.AuthUserRepositoryPort;
import com.pe.pivot.auth.domain.port.out.JwtTokenPort;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Pure Orchestrator Use Case for registering a new AuthUser and issuing an initial JWT.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RegisterUserUseCaseImpl implements RegisterUserUseCase {

  private final AuthUserRepositoryPort userRepository;
  private final AuthUserDomainInitializer userInitializer;
  private final JwtTokenPort tokenPort;

  @Override
  public Single<AuthToken> registerUser(AuthUser user, String rawPassword){
    String normalizedUsername = userInitializer.normalize(user.getUsername());
    String normalizedEmail = userInitializer.normalize(user.getEmail());

    return Single.zip(
            userRepository.existsByUsername(normalizedUsername),
            userRepository.existsByEmail(normalizedEmail),
            (usernameExists, emailExists) -> {
              if (Boolean.TRUE.equals(usernameExists)) {
                throw new DuplicateUserException(
                        "Username is already registered: " + normalizedUsername);
              }
              if (Boolean.TRUE.equals(emailExists)) {
                throw new DuplicateUserException(
                        "Email is already registered: " + normalizedEmail);
              }

              return userInitializer.initialize(user, rawPassword);
            })
            .flatMap(userRepository::save)
            .map(tokenPort::generateToken)
            .doOnSuccess(token -> log.info(
                    "User registered and JWT issued for username: {}", token.username()));
  }
}
