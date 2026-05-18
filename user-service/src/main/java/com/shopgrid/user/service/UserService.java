package com.shopgrid.user.service;

import com.shopgrid.user.domain.model.AccountStatus;
import com.shopgrid.user.dto.request.UpdateUserRequest;
import com.shopgrid.user.dto.request.UserRequest;
import com.shopgrid.user.dto.response.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface UserService {

    UserResponse create(UserRequest request);
    UserResponse get(String email);
    UserResponse update(String email, UpdateUserRequest request);
    UserResponse getById(UUID id);
    Page<UserResponse> getAll(Pageable pageable);
    void updateStatus(UUID id, AccountStatus status);

}
