package com.pe.pivot.auth.infrastructure.adapter.in.web.dto;

import com.pe.pivot.auth.domain.model.Role;
import java.time.Instant;
import lombok.Builder;

/**
 * Response payload containing the issued JWT token and user metadata.
 */
@Builder
public record AuthResponse(
        String accessToken,
        String tokenType,
        long expiresInMs,
        String username,
        Role role,
        Instant issuedAt
) {
}