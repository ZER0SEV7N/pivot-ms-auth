package com.pe.pivot.auth.domain.model;

import java.time.Instant;
import lombok.Builder;

/**
 * Domain value object encapsulating an issued Bearer JWT token and its metadata.
 */
@Builder
public record AuthToken(
        String accessToken,
        String tokenType,
        long expiresInMs,
        String username,
        Role role,
        Instant issuedAt
) {
}