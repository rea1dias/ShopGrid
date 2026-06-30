package com.shopgrid.payment.event;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentRefundedEvent(
        UUID refundId,
        UUID orderId,
        UUID userId,
        BigDecimal refundAmount
) {}