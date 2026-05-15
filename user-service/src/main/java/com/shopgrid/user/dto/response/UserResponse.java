package com.shopgrid.user.dto.response;

import com.shopgrid.user.domain.model.AccountStatus;
import com.shopgrid.user.domain.model.Role;

import java.util.UUID;

public record UserResponse(
        UUID id,
        String firstName,
        String lastName,
        String email,
        Role role,
        AccountStatus status,
        String phoneNumber
) {}