package com.pe.pivot.auth.domain.port.in;

import com.pe.pivot.auth.domain.model.AuthToken;
import com.pe.pivot.auth.domain.model.AuthUser;
import io.reactivex.rxjava3.core.Single;

/**
 * Use case interface for registering a new user in the authentication system.
 * This interface defines the contract for user registration operations, including
 * validation and persistence of user data.
 */
public interface RegisterUserUseCase {

  /**
   * Registers a new user with the provided AuthUser details and raw password.
   *
   * @param user - the AuthUser object containing user details to be registered
   * @param rawPassword - the raw password for the user, which will be securely
   *        hashed before storage
   * @return - a Single emitting the AuthToken of the newly registered user
   */
  Single<AuthToken> registerUser(AuthUser user, String rawPassword);

}
