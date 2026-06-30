package com.shopgrid.admin.service.impl;

import com.shopgrid.admin.domain.dto.request.RejectRequest;
import com.shopgrid.admin.domain.dto.response.PageResponse;
import com.shopgrid.admin.domain.dto.response.RefundResponse;
import com.shopgrid.admin.domain.dto.response.UserResponse;
import com.shopgrid.admin.rest.OrderServiceClient;
import com.shopgrid.admin.rest.UserServiceClient;
import com.shopgrid.admin.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final UserServiceClient userServiceRestClient;
    private final OrderServiceClient orderServiceRestClient;

    @Override
    public void blockUser(UUID id, String token) {
        userServiceRestClient.blockUser(id, token);
    }

    @Override
    public UserResponse get(UUID id, String token) {
        return userServiceRestClient.getUser(id, token);
    }

    @Override
    public PageResponse<RefundResponse> getAllRefunds(String token) {
        return orderServiceRestClient.getAllRefunds(token);
    }

    @Override
    public RefundResponse rejectRefund(UUID id, String token, RejectRequest request) {
        return orderServiceRestClient.rejectRefund(token, id, request);
    }

    @Override
    public RefundResponse approveRefund(UUID id, String token) {
        return orderServiceRestClient.approveRefund(token, id);
    }
}
