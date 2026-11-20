package com.pe.pivot.auth.infrastructure.adapter.in.web.dto;

import com.pe.pivot.auth.domain.model.Role;
import java.time.Instant;
import lombok.Builder;

/**
 * Response payload describing the validity and claims of a verified JWT token.
 */
@Builder
public record TokenValidationResponse(
        boolean valid,
        String username,
        Role role,
        Instant expiresAt
) {
}