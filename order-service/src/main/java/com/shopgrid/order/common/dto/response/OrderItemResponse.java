package com.shopgrid.order.common.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderItemResponse(

        UUID productId,
        String productName,
        BigDecimal price,
        Integer quantity
) {
}
