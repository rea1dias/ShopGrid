package com.shopgrid.admin.service.impl;

import com.shopgrid.admin.rest.UserServiceClient;
import com.shopgrid.admin.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserServiceClient userServiceRestClient;

    @Override
    public void blockUser(UUID id, String token) {userServiceRestClient.blockUser(id, token);}
}
