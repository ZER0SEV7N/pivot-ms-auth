package com.pe.pivot.auth.infrastructure.adapter.in.web.dto;

import java.time.Instant;
import lombok.Builder;

/**
 * Standardized API error response body matching the OpenAPI contract.
 */
@Builder
public record ErrorResponse(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path
) {
}