package com.shopgrid.admin.domain.dto.response;

import com.shopgrid.admin.domain.enums.RefundReason;
import com.shopgrid.admin.domain.enums.RefundStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record RefundResponse(
        UUID id,
        UUID orderId,
        UUID userId,
        RefundStatus status,
        List<RefundItemResponse> refundItems,
        BigDecimal refundAmount,
        Instant refundCreatedAt,
        RefundReason reason,
        String comment
) {
}
