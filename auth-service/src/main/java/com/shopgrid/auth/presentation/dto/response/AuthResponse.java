package com.shopgrid.auth.presentation.dto.response;

import java.time.Instant;
import java.util.UUID;

public record AuthResponse(
        UUID userId,
        String email,
        String role,
        String accessToken,
        Instant expiresAt
) {
}
