package com.shopgrid.order.common.exception;

import java.util.UUID;

public class InvalidRefundStatusException extends RuntimeException {
    public InvalidRefundStatusException(UUID id) {
        super("Refund of order " + id + " is not in REQUESTED status");
    }
}
