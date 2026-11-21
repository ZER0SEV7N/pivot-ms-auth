package com.pe.pivot.auth.infrastructure.adapter.in.web;

import com.pe.pivot.auth.domain.port.in.LoginUserUseCase;
import com.pe.pivot.auth.domain.port.in.RegisterUserUseCase;
import com.pe.pivot.auth.domain.port.in.ValidateTokenUseCase;
import com.pe.pivot.auth.infrastructure.adapter.in.web.dto.AuthResponse;
import com.pe.pivot.auth.infrastructure.adapter.in.web.dto.LoginRequest;
import com.pe.pivot.auth.infrastructure.adapter.in.web.dto.RegisterRequest;
import com.pe.pivot.auth.infrastructure.adapter.in.web.dto.TokenValidationResponse;
import com.pe.pivot.auth.infrastructure.adapter.in.web.mapper.AuthWebMapper;
import io.reactivex.rxjava3.core.Single;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller implementing auth-api.yaml with RxJava 3.
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

  private final RegisterUserUseCase registerUserUseCase;
  private final LoginUserUseCase loginUserUseCase;
  private final ValidateTokenUseCase validateTokenUseCase;
  private final AuthWebMapper mapper;

  /**
   * Registers a new user based on the provided RegisterRequest.
   *
   * @param request - the RegisterRequest containing the details for the new user
   * @return - a Single emitting an AuthResponse with the generated JWT token
   */
  @PostMapping("/register")
  @ResponseStatus(HttpStatus.CREATED)
  public Single<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
    return registerUserUseCase.registerUser(mapper.toDomain(request), request.password())
            .map(mapper::toAuthResponse);
  }

  /**
   * Authenticates a user based on the provided LoginRequest.
   *
   * @param request - the LoginRequest containing the username and password
   * @return - a Single emitting an AuthResponse with the generated JWT token
   */
  @PostMapping("/login")
  public Single<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
    return loginUserUseCase.login(request.username(), request.password())
            .map(mapper::toAuthResponse);
  }

  /**
   * Validates the provided JWT token from the Authorization header.
   *
   * @param authHeader - the Authorization header containing the JWT token to validate
   * @return - a Single emitting a TokenValidationResponse indicating token validity
   */
  @GetMapping("/validate")
  public Single<TokenValidationResponse> validate(
          @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authHeader) {
    return validateTokenUseCase.validate(authHeader)
            .map(mapper::toValidationResponse);
  }
}