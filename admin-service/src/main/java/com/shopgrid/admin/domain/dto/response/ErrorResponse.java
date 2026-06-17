package com.shopgrid.admin.domain.dto.response;

import java.time.Instant;

public record ErrorResponse(
        int code,
        String message,
        Instant timestamp)
{}
