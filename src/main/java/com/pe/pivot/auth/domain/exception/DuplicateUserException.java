package com.pe.pivot.auth.domain.exception;

/**
 * Custom exception thrown when attempting to create a user that already exists in the system.
 * This exception is used to indicate a violation of unique user constraints, such as duplicate email or username.
 * It extends RuntimeException, allowing it to be thrown without being explicitly declared in method signatures.
 */
public class DuplicateUserException extends RuntimeException {
  public DuplicateUserException(String message) {
    super(message);
  }
}
