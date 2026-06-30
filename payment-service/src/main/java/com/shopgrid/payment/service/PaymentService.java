package com.shopgrid.payment.service;

import com.shopgrid.payment.event.PaymentRequestedEvent;
import com.shopgrid.payment.event.RefundApprovedEvent;

public interface PaymentService {

    void processPayment(PaymentRequestedEvent event);

    void refundPayment(RefundApprovedEvent event);
}
