package com.shopgrid.payment.service;

import com.shopgrid.payment.event.PaymentRequestedEvent;

public interface PaymentService {

    void processPayment(PaymentRequestedEvent event);
}
