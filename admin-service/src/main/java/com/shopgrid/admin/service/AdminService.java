package com.shopgrid.admin.service;

import com.shopgrid.admin.domain.dto.request.RejectRequest;
import com.shopgrid.admin.domain.dto.response.PageResponse;
import com.shopgrid.admin.domain.dto.response.RefundResponse;
import com.shopgrid.admin.domain.dto.response.UserResponse;

import java.util.UUID;

public interface AdminService {

    void blockUser(UUID id, String token);

    UserResponse get(UUID id, String token);

    PageResponse<RefundResponse> getAllRefunds(String token);

    RefundResponse rejectRefund(UUID id, String token, RejectRequest request);

    RefundResponse approveRefund(UUID id, String token);

}
