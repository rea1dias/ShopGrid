package com.shopgrid.order.common.dto.request;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductInfo(
        UUID id,
        String name,
        BigDecimal price
) {
}