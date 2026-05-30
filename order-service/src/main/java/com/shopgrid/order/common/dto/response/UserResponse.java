package com.shopgrid.order.common.dto.response;

import com.shopgrid.order.common.enums.AccountStatus;
import com.shopgrid.order.common.enums.Role;

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