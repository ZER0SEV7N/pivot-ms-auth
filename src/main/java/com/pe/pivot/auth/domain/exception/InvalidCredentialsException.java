package com.pe.pivot.auth.domain.exception;

/**
 * Custom exception thrown when invalid authentication credentials are provided.
 * This exception indicates that the supplied credentials do not meet the
 * expected criteria for authentication.
 */
public class InvalidCredentialsException extends RuntimeException {
  public InvalidCredentialsException(String message) {
    super(message);
  }
}
