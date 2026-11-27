package com.pe.pivot.auth.infrastructure.adapter.in.web.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.pe.pivot.auth.domain.exception.DuplicateUserException;
import com.pe.pivot.auth.domain.exception.InvalidCredentialsException;
import com.pe.pivot.auth.domain.exception.InvalidTokenException;
import com.pe.pivot.auth.infrastructure.adapter.in.web.dto.ErrorResponse;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.support.WebExchangeBindException;

/**
 * Unit test suite for GlobalExceptionHandler in ms-auth.
 */
class GlobalExceptionHandlerTest {

  private GlobalExceptionHandler handler;
  private MockServerHttpRequest request;

  @BeforeEach
  void setUp() {
    handler = new GlobalExceptionHandler();
    request = MockServerHttpRequest.post("/api/v1/auth/register").build();
  }

  @Test
  @DisplayName("Should return 409 Conflict when DuplicateUserException is thrown")
  void shouldReturnConflictOnDuplicateUser() {
    // Arrange
    DuplicateUserException ex = new DuplicateUserException("Username already exists");

    // Act
    ResponseEntity<ErrorResponse> response = handler.handleDuplicateUser(ex, request);

    // Assert
    assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(409, response.getBody().status());
    assertEquals("Username already exists", response.getBody().message());
    assertEquals("/api/v1/auth/register", response.getBody().path());
  }

  @Test
  @DisplayName("Should return 401 Unauthorized when InvalidCredentialsException is thrown")
  void shouldReturnUnauthorizedOnInvalidCredentials() {
    // Arrange
    InvalidCredentialsException ex = new InvalidCredentialsException("Invalid credentials");

    // Act
    ResponseEntity<ErrorResponse> response = handler.handleInvalidCredentials(ex, request);

    // Assert
    assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(401, response.getBody().status());
    assertEquals("Invalid credentials", response.getBody().message());
  }

  @Test
  @DisplayName("Should return 401 Unauthorized when InvalidTokenException is thrown")
  void shouldReturnUnauthorizedOnInvalidToken() {
    // Arrange
    InvalidTokenException ex = new InvalidTokenException("Token expired");

    // Act
    ResponseEntity<ErrorResponse> response = handler.handleInvalidToken(ex, request);

    // Assert
    assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(401, response.getBody().status());
    assertEquals("Token expired", response.getBody().message());
  }

  @Test
  @DisplayName("Should return 400 Bad Request when WebExchangeBindException is thrown")
  void shouldReturnBadRequestOnValidationError() {
    // Arrange
    WebExchangeBindException ex = mock(WebExchangeBindException.class);
    FieldError fieldError = new FieldError("registerRequest", "email", "must be a valid email");
    when(ex.getFieldErrors()).thenReturn(List.of(fieldError));

    // Act
    ResponseEntity<ErrorResponse> response = handler.handleValidation(ex, request);

    // Assert
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(400, response.getBody().status());
    assertTrue(response.getBody().message().contains("email: must be a valid email"));
  }

  @Test
  @DisplayName("Should return 500 Internal Server Error on unexpected exceptions")
  void shouldReturnInternalServerErrorOnGenericException() {
    // Arrange
    RuntimeException ex = new RuntimeException("Unexpected failure");

    // Act
    ResponseEntity<ErrorResponse> response = handler.handleGeneric(ex, request);

    // Assert
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(500, response.getBody().status());
    assertEquals("An unexpected internal error occurred.", response.getBody().message());
  }
}