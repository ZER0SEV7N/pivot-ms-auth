package com.pe.pivot.auth.infrastructure.adapter.in.web.mapper;

import com.pe.pivot.auth.domain.model.AuthToken;
import com.pe.pivot.auth.domain.model.AuthUser;
import com.pe.pivot.auth.domain.model.TokenClaims;
import com.pe.pivot.auth.infrastructure.adapter.in.web.dto.AuthResponse;
import com.pe.pivot.auth.infrastructure.adapter.in.web.dto.RegisterRequest;
import com.pe.pivot.auth.infrastructure.adapter.in.web.dto.TokenValidationResponse;
import org.springframework.stereotype.Component;

/**
 * Web Mapper translating between REST DTOs and Authentication domain models (SRP).
 */
@Component
public class AuthWebMapper {

  /**
   * Converts a RegisterRequest DTO into an AuthUser domain object.
   *
   * @param request - the registration request DTO to convert
   * @return - the corresponding AuthUser domain object
   */
  public AuthUser toDomain(RegisterRequest request) {
    return AuthUser.builder()
            .username(request.username())
            .email(request.email())
            .role(request.role())
            .customerId(request.customerId())
            .build();
  }

  /**
   * Converts an AuthToken domain object into an AuthResponse DTO for REST responses.
   *
   * @param token - the authentication token domain object to convert
   * @return - the corresponding AuthResponse DTO
   */
  public AuthResponse toAuthResponse(AuthToken token) {
    return AuthResponse.builder()
            .accessToken(token.accessToken())
            .tokenType(token.tokenType())
            .expiresInMs(token.expiresInMs())
            .username(token.username())
            .role(token.role())
            .issuedAt(token.issuedAt())
            .build();
  }

  /**
   * Converts TokenClaims domain object into a TokenValidationResponse DTO for REST responses.
   *
   * @param claims - the token claims domain object to convert
   * @return - the corresponding TokenValidationResponse DTO
   */
  public TokenValidationResponse toValidationResponse(TokenClaims claims) {
    return TokenValidationResponse.builder()
            .valid(claims.valid())
            .username(claims.username())
            .role(claims.role())
            .expiresAt(claims.expiresAt())
            .build();
  }
}