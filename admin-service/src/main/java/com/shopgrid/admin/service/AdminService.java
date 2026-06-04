package com.shopgrid.admin.service;

import com.shopgrid.admin.domain.dto.response.UserResponse;

import java.util.UUID;

public interface AdminService {

    void blockUser(UUID id, String token);
    UserResponse get(UUID id, String token);
}
