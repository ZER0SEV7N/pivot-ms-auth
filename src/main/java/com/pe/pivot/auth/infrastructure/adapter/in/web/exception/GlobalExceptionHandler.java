package com.pe.pivot.auth.infrastructure.adapter.in.web.exception;

import com.pe.pivot.auth.domain.exception.DuplicateUserException;
import com.pe.pivot.auth.domain.exception.InvalidCredentialsException;
import com.pe.pivot.auth.domain.exception.InvalidTokenException;
import com.pe.pivot.auth.infrastructure.adapter.in.web.dto.ErrorResponse;
import java.time.Instant;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;

/**
 * Global reactive exception handler translating domain and validation errors into HTTP responses.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  /**
   * Handles duplicate username or email registration attempts.
   *
   * @param ex duplicate user exception
   * @param request current HTTP request
   * @return 409 Conflict error response
   */
  @ExceptionHandler(DuplicateUserException.class)
  public ResponseEntity<ErrorResponse> handleDuplicateUser(
          DuplicateUserException ex, ServerHttpRequest request) {
    log.warn("Duplicate user conflict: {}", ex.getMessage());
    return buildResponse(HttpStatus.CONFLICT, ex.getMessage(), request);
  }

  /**
   * Handles invalid login credentials or inactive accounts.
   *
   * @param ex invalid credentials exception
   * @param request current HTTP request
   * @return 401 Unauthorized error response
   */
  @ExceptionHandler(InvalidCredentialsException.class)
  public ResponseEntity<ErrorResponse> handleInvalidCredentials(
          InvalidCredentialsException ex, ServerHttpRequest request) {
    log.warn("Authentication failed: {}", ex.getMessage());
    return buildResponse(HttpStatus.UNAUTHORIZED, ex.getMessage(), request);
  }

  /**
   * Handles missing, expired, or tampered JWT tokens.
   *
   * @param ex invalid token exception
   * @param request current HTTP request
   * @return 401 Unauthorized error response
   */
  @ExceptionHandler(InvalidTokenException.class)
  public ResponseEntity<ErrorResponse> handleInvalidToken(
          InvalidTokenException ex, ServerHttpRequest request) {
    log.warn("Token validation failed: {}", ex.getMessage());
    return buildResponse(HttpStatus.UNAUTHORIZED, ex.getMessage(), request);
  }

  /**
   * Handles Bean Validation errors on incoming WebFlux request payloads.
   *
   * @param ex validation exception
   * @param request current HTTP request
   * @return 400 Bad Request error response
   */
  @ExceptionHandler(WebExchangeBindException.class)
  public ResponseEntity<ErrorResponse> handleValidation(
          WebExchangeBindException ex, ServerHttpRequest request) {
    String details = ex.getFieldErrors().stream()
            .map(err -> err.getField() + ": " + err.getDefaultMessage())
            .collect(Collectors.joining("; "));
    log.warn("Payload validation error: {}", details);
    return buildResponse(HttpStatus.BAD_REQUEST, details, request);
  }

  /**
   * Handles any unexpected server error.
   *
   * @param ex unexpected exception
   * @param request current HTTP request
   * @return 500 Internal Server Error response
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGeneric(Exception ex, ServerHttpRequest request) {
    log.error("Unexpected error in ms-auth: ", ex);
    return buildResponse(
            HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected internal error occurred.", request);
  }

  private ResponseEntity<ErrorResponse> buildResponse(
          HttpStatus status, String message, ServerHttpRequest request) {
    ErrorResponse body = ErrorResponse.builder()
            .timestamp(Instant.now())
            .status(status.value())
            .error(status.getReasonPhrase())
            .message(message)
            .path(request.getPath().value())
            .build();
    return ResponseEntity.status(status).body(body);
  }
}