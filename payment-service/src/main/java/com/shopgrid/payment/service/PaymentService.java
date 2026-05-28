package com.shopgrid.payment.service;

import com.shopgrid.payment.event.StockReservedEvent;

public interface PaymentService {

    void processPayment(StockReservedEvent event);
}
