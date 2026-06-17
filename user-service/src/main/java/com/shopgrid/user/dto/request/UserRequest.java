package com.shopgrid.user.dto.request;

import com.shopgrid.user.domain.model.AccountStatus;
import com.shopgrid.user.domain.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record UserRequest(
        UUID id,

        @NotBlank(message = "First name must not be blank")
        String firstName,

        @NotBlank(message = "Last name must not be blank")
        String lastName,

        @NotBlank(message = "Email must not be blank")
        @Email(message = "Email must be valid")
        String email,

        @NotNull(message = "Role must not be null")
        Role role,

        @NotNull(message = "Status must not be null")
        AccountStatus status,

        String phoneNumber
) {}
