package com.shopgrid.auth.presentation.dto.request;

public record UpdateRequest(
        String email,
        String firstName,
        String lastName
) {}
