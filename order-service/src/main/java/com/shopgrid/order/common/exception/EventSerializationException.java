package com.shopgrid.order.common.exception;

import java.util.UUID;

public class EventSerializationException extends RuntimeException {
    public EventSerializationException(UUID orderId) {
        super("Failed to serialize for orderId: " + orderId);
    }
}
