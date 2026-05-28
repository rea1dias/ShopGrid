package com.shopgrid.payment.domain.dto.response;

import com.shopgrid.payment.common.enums.PaymentCurrency;
import com.shopgrid.payment.common.enums.PaymentStatus;
import com.shopgrid.payment.common.enums.ProviderType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PaymentResponse(
        UUID id,
        UUID orderId,
        UUID userId,
        BigDecimal amount,
        PaymentCurrency currency,
        PaymentStatus status,
        ProviderType provider,
        Instant createdAt,
        Instant updatedAt
) {
}
