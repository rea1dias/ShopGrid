package com.shopgrid.auth.application;

import com.shopgrid.auth.domain.model.AccountStatus;
import com.shopgrid.auth.domain.model.Role;
import com.shopgrid.auth.infrastructure.persistence.entity.AuthUser;
import com.shopgrid.auth.infrastructure.persistence.repository.AuthUserRepository;
import com.shopgrid.auth.infrastructure.security.JwtService;
import com.shopgrid.auth.presentation.dto.request.LoginRequest;
import com.shopgrid.auth.presentation.dto.request.RegisterRequest;
import com.shopgrid.auth.presentation.dto.response.AuthResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Locale;

@Service
public class AuthService {

    private final AuthUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(
            AuthUserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

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
        return createAuthResponse(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        String email = normalizeEmail(request.email());

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, request.password())
        );

        AuthUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Authenticated user was not found"));

        return createAuthResponse(user);
    }

    private AuthResponse createAuthResponse(AuthUser user) {
        Instant expiresAt = jwtService.expiresAt();
        String token = jwtService.generateToken(user, expiresAt);

        return new AuthResponse(
                user.getId(),
                user.getEmail(),
                user.getRole().name(),
                token,
                expiresAt
        );
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
