package com.shopgrid.order.event;

import java.util.UUID;

public record StockReservedEvent(
        UUID orderId,
        UUID userId
) {}
