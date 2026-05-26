package com.shopgrid.order.common.dto.response;

import java.time.Instant;

public record ErrorResponse(
        int code,
        String message,
        Instant timestamp
) {}
