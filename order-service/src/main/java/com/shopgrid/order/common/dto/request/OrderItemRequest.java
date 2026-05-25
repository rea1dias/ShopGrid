package com.shopgrid.order.common.dto.request;

import java.util.UUID;

public record OrderItemRequest(

        UUID productId,
        Integer quantity
) {}
