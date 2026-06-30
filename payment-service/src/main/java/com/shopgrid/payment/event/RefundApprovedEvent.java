package com.shopgrid.payment.event;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record RefundApprovedEvent(
        UUID refundId,
        UUID orderId,
        UUID userId,
        BigDecimal refundAmount,
        List<RefundItemEvent> items
) {
}
