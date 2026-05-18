package com.shopgrid.auth.presentation.controller;

import com.shopgrid.auth.application.AuthService;
import com.shopgrid.auth.presentation.dto.request.LoginRequest;
import com.shopgrid.auth.presentation.dto.request.RefreshTokenRequest;
import com.shopgrid.auth.presentation.dto.request.RegisterRequest;
import com.shopgrid.auth.presentation.dto.request.UpdateRequest;
import com.shopgrid.auth.presentation.dto.response.AuthResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/refresh")
    public AuthResponse refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return authService.refreshToken(request);
    }

    @PutMapping("/internal/update")
    public void update(@Valid @RequestBody UpdateRequest request) {
        authService.update(request);
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(@Valid @RequestBody RefreshTokenRequest request) {
        authService.logout(request);
    }

    @GetMapping("/me")
    public AuthResponse me(Authentication authentication, @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring("Bearer ".length());
        return authService.me(authentication.getName(), token);
    }
}
