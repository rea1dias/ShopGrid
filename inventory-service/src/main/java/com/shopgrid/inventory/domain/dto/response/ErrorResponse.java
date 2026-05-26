package com.shopgrid.inventory.domain.dto.response;

import java.time.Instant;

public record ErrorResponse(
        int code,
        String message,
        Instant timestamp
) {}
