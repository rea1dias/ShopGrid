package com.shopgrid.cart.common.response;

import java.time.Instant;

public record ErrorResponse(
        int code,
        String message,
        Instant timestamp) {
}
