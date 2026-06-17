package com.shopgrid.order.event;

import com.shopgrid.order.common.enums.ChannelType;
import com.shopgrid.order.common.enums.NotificationTemplateType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

public record NotificationEvent(

        @NotBlank
        String eventId,

        @NotBlank
        UUID userId,

        @NotNull
        UUID orderId,

        @NotNull
        ChannelType channel,

        @NotBlank
        NotificationTemplateType template,

        @NotBlank
        String recipient,

        @NotNull
        BigDecimal totalPrice,

        @NotNull
        Map<String, Object> context
) {
}