package com.pe.pivot.auth.domain.model;

import java.time.Instant;
import lombok.Builder;

/**
 * Domain value object holding verified claims extracted from a JWT token.
 */
@Builder
public record TokenClaims(
        boolean valid,
        String username,
        Role role,
        String customerId,
        Instant expiresAt
) {
}