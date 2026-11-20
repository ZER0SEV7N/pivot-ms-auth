package com.pe.pivot.auth.domain.port.in;

import com.pe.pivot.auth.domain.model.AuthToken;
import io.reactivex.rxjava3.core.Single;

/**
 * Use case interface for logging in a user. This interface defines the contract for the login operation,
 * which involves validating user credentials and returning an authentication token upon successful login.
 */
public interface LoginUserUseCase {

  /**
   * Attempts to log in a user with the provided username and password. If the credentials are valid,
   *
   * @param username - the username of the user attempting to log in
   * @param password - the password of the user attempting to log in
   * @return - a Single emitting an AuthToken if the login is successful, or an error if the credentials are invalid
   */
  Single<AuthToken> login(String username, String password);
}
