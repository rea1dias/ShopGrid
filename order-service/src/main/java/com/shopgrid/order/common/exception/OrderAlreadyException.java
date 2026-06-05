package com.shopgrid.order.common.exception;

import java.util.UUID;

public class OrderAlreadyException extends RuntimeException {
    public OrderAlreadyException(UUID id) {
        super("Order already exists: " + id);
    }
}
