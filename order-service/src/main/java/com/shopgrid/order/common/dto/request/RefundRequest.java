package com.shopgrid.order.common.dto.request;

import com.shopgrid.order.common.enums.RefundReason;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record RefundRequest(

        @NotNull UUID orderId,
        List<UUID> orderItemIds,
        @NotNull RefundReason reason,
        String comment
) {
}
