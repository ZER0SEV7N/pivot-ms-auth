package com.pe.pivot.auth.domain.exception;

/**
 * Custom exception thrown when an invalid or expired authentication token is encountered.
 * This exception is used to indicate that the provided token does not meet the expected criteria for authentication or authorization.
 */
public class InvalidCredentialsException extends RuntimeException {
  public InvalidCredentialsException(String message) {
    super(message);
  }
}
