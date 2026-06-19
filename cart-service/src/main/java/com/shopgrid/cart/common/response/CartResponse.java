package com.shopgrid.cart.common.response;

import com.shopgrid.cart.domain.CartItem;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record CartResponse(

        UUID userId,
        List<CartItem> cartItems,
        BigDecimal totalPrice
) {
}
