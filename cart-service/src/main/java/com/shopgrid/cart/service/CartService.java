package com.shopgrid.cart.service;

import com.shopgrid.cart.common.request.AddItemRequest;
import com.shopgrid.cart.common.response.CartResponse;

import java.util.UUID;

public interface CartService {

    CartResponse get(UUID userId);
    CartResponse add(UUID userId, AddItemRequest request);
    CartResponse update(UUID userId, UUID productId, int quantity);
    CartResponse remove(UUID userId, UUID productId);
    void clear(UUID userId);


}
