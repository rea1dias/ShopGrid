package com.shopgrid.auth.application;

import com.shopgrid.auth.domain.model.AccountStatus;
import com.shopgrid.auth.domain.model.Role;
import com.shopgrid.auth.infrastructure.persistence.entity.AuthUser;
import com.shopgrid.auth.infrastructure.persistence.entity.RefreshToken;
import com.shopgrid.auth.infrastructure.persistence.repository.AuthUserRepository;
import com.shopgrid.auth.infrastructure.persistence.repository.RefreshTokenRepository;
import com.shopgrid.auth.infrastructure.security.JwtService;
import com.shopgrid.auth.presentation.dto.request.LoginRequest;
import com.shopgrid.auth.presentation.dto.request.RefreshTokenRequest;
import com.shopgrid.auth.presentation.dto.request.RegisterRequest;
import com.shopgrid.auth.presentation.dto.request.UpdateRequest;
import com.shopgrid.auth.presentation.dto.response.AuthResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final int REFRESH_TOKEN_BYTES = 64;

    private final AuthUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserServiceClient userServiceClient;
    private final AuthUserRepository authUserRepository;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String email = normalizeEmail(request.email());

        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyUsedException(email);
        }

        AuthUser user = new AuthUser(
                request.firstName(),
                request.lastName(),
                email,
                passwordEncoder.encode(request.password()),
                Role.USER,
                AccountStatus.ACTIVE
        );
        AuthUser saved = userRepository.save(user);
        userServiceClient.createUser(saved.getFirstName(), saved.getLastName(), saved.getEmail());
        return createAuthResponse(saved);
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        String email = normalizeEmail(request.email());

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, request.password())
        );

        AuthUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Authenticated user was not found"));

        return createAuthResponse(user);
    }

    @Transactional(readOnly = true)
    public AuthResponse me(String email, String token) {
        AuthUser user = userRepository.findByEmail(normalizeEmail(email))
                .orElseThrow(() -> new IllegalStateException("Authenticated user was not found"));

        return AuthResponse.from(user, token, jwtService.extractExpiration(token));
    }

    private AuthResponse createAuthResponse(AuthUser user) {
        Instant expiresAt = jwtService.expiresAt();
        String token = jwtService.generateToken(user, expiresAt);

        String refreshToken = generateOpaqueToken();
        Instant refreshTokenExpiresAt = jwtService.refreshTokenExpiresAt();
        refreshTokenRepository.save(new RefreshToken(user, hashToken(refreshToken), refreshTokenExpiresAt));

        return AuthResponse.withTokens(user, token, expiresAt, refreshToken, refreshTokenExpiresAt);
    }

    @Transactional
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        String tokenHash = hashToken(request.refreshToken());
        var storedToken = refreshTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new UnauthorizedException("Refresh token was not found"));

        Instant now = Instant.now();
        if (!storedToken.isActive(now)) {
            throw new UnauthorizedException("Invalid refresh token");
        }

        storedToken.revoke(now);

        return createAuthResponse(storedToken.getUser());
    }

    @Transactional
    public void logout(RefreshTokenRequest request) {
        String tokenHash = hashToken(request.refreshToken());
        refreshTokenRepository.findByTokenHash(tokenHash)
                .filter(token -> token.isActive(Instant.now()))
                .ifPresent(token -> token.revoke(Instant.now()));
    }

    @Transactional
    public void update(UpdateRequest request) {
        AuthUser user = authUserRepository.findByEmail(request.email())
                .orElseThrow(() -> new NotFoundException("Authenticated user was not found"));
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private String hashToken(String token) {
        try {
            var digest = MessageDigest.getInstance("SHA-256")
                    .digest(token.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(digest);
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 is not available", ex);
        }
    }

    private String generateOpaqueToken() {
        byte[] bytes = new byte[REFRESH_TOKEN_BYTES];
        SECURE_RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
