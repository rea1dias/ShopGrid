package com.shopgrid.admin.domain.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public record RefundItemResponse(

        UUID id,
        UUID productId,
        Integer quantity,
        BigDecimal price
) {
}
