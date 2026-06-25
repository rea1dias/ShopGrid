package com.shopgrid.order.common.exception;

import java.util.UUID;

public class CannotCancelAllItemsException extends RuntimeException {
    public CannotCancelAllItemsException(UUID orderId) {
        super("Cannot cancel all items in order " + orderId + " — use full order cancellation instead");
    }
}
