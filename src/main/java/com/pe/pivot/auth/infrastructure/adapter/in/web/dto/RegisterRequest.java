package com.pe.pivot.auth.infrastructure.adapter.in.web.dto;

import com.pe.pivot.auth.domain.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

/**
 * Request payload for registering a new user account.
 */
@Builder
public record RegisterRequest(
        @NotBlank(message = "username is required")
        @Size(min = 4, max = 50, message = "username must be between 4 and 50 characters")
        String username,
        @NotBlank(message = "email is required")
        @Email(message = "email must have a valid format")
        String email,
        @NotBlank(message = "password is required")
        @Size(min = 8, message = "password must be at least 8 characters long")
        String password,
        Role role,
        String customerId
) {
}