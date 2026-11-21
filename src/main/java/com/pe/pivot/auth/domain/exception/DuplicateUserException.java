package com.pe.pivot.auth.domain.exception;

/**
 * Custom exception thrown when attempting to create a user that already exists.
 * This exception indicates a violation of unique user constraints, such as a
 * duplicate email or username. It extends RuntimeException, allowing it to be
 * thrown without being explicitly declared in method signatures.
 */
public class DuplicateUserException extends RuntimeException {
  public DuplicateUserException(String message) {
    super(message);
  }
}
