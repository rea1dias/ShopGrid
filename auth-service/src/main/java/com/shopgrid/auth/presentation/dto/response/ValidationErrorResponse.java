package com.shopgrid.auth.presentation.dto.response;

import java.time.Instant;
import java.util.Map;

public record ValidationErrorResponse(
        String code,
        String message,
        Map<String, String> fields,
        Instant timestamp
) {
}
