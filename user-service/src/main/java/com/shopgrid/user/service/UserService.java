package com.shopgrid.user.service;

import com.shopgrid.user.dto.request.UserRequest;
import com.shopgrid.user.dto.response.UserResponse;

public interface UserService {

    UserResponse create(UserRequest request);
}
