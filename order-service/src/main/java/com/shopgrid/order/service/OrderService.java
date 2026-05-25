package com.shopgrid.order.service;

import com.shopgrid.order.common.dto.request.OrderRequest;
import com.shopgrid.order.common.dto.response.OrderResponse;

import java.util.UUID;

public interface OrderService {

    OrderResponse create(OrderRequest request, UUID userId);

}
