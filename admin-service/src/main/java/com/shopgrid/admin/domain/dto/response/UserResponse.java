package com.shopgrid.admin.domain.dto.response;

import com.shopgrid.admin.domain.enums.AccountStatus;
import com.shopgrid.admin.domain.enums.Role;

import java.util.UUID;

public record UserResponse(
        UUID id,
        String firstName,
        String lastName,
        String email,
        Role role,
        AccountStatus status,
        String phoneNumber
) {
}
