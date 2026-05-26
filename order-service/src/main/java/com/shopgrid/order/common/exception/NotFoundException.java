package com.shopgrid.order.common.exception;

import java.util.UUID;

public class NotFoundException extends RuntimeException {

    public NotFoundException(UUID orderId) {
        super("Order with id " + orderId + " not found");
    }
}
