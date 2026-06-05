package com.shopgrid.order.service;

import com.shopgrid.order.common.dto.request.OrderRequest;
import com.shopgrid.order.common.dto.response.OrderResponse;
import com.shopgrid.order.common.enums.CancelReason;
import com.shopgrid.order.common.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface OrderService {

    OrderResponse create(OrderRequest request, UUID userId);
    void update(UUID id, OrderStatus status);
    OrderResponse get(UUID orderId, UUID userId);
    Page<OrderResponse> getMyOrders(UUID userId, Pageable pageable);
    OrderResponse cancel(UUID orderId, UUID userId, CancelReason reason);


}
