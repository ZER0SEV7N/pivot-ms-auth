package com.pe.pivot.auth.infrastructure.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

/**
 * Request payload for authenticating user credentials.
 */
@Builder
public record LoginRequest(
        @NotBlank(message = "username is required") String username,
        @NotBlank(message = "password is required") String password
) {
}