package com.shopgrid.auth.presentation.dto.response;

import com.shopgrid.auth.infrastructure.persistence.entity.AuthUser;

import java.time.Instant;
import java.util.UUID;

public record AuthResponse(
        UUID userId,
        String email,
        String role,
        String accessToken,
        Instant accessTokenExpiresAt,
        String refreshToken,
        Instant refreshTokenExpiresAt
) {
    public static AuthResponse from(AuthUser user, String accessToken, Instant expiresAt) {
        return new AuthResponse(
                user.getId(),
                user.getEmail(),
                user.getRole().name(),
                accessToken,
                expiresAt,
                null,
                null
        );
    }

    public static AuthResponse withTokens(
            AuthUser user,
            String accessToken,
            Instant accessTokenExpiresAt,
            String refreshToken,
            Instant refreshTokenExpiresAt
    ) {
        return new AuthResponse(
                user.getId(),
                user.getEmail(),
                user.getRole().name(),
                accessToken,
                accessTokenExpiresAt,
                refreshToken,
                refreshTokenExpiresAt
        );
    }
}
