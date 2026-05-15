package com.shopgrid.auth.presentation.dto.response;

import com.shopgrid.auth.infrastructure.persistence.entity.AuthUser;

import java.time.Instant;
import java.util.UUID;

public record AuthResponse(
        UUID userId,
        String email,
        String role,
        String accessToken,
        Instant expiresAt
) {
    public static AuthResponse from(AuthUser user, String accessToken, Instant expiresAt) {
        return new AuthResponse(
                user.getId(),
                user.getEmail(),
                user.getRole().name(),
                accessToken,
                expiresAt
        );
    }
}
