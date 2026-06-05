package com.shopgrid.payment.service.impl;

import com.shopgrid.payment.domain.entity.Payment;
import com.shopgrid.payment.event.PaymentCompletedEvent;
import com.shopgrid.payment.event.StockReservedEvent;
import com.shopgrid.payment.kafka.PaymentEventPublisher;
import com.shopgrid.payment.repo.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceImplTests {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentEventPublisher paymentEventPublisher;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    @Test
    public void paymentSuccess() {
        StockReservedEvent event = new StockReservedEvent(
                UUID.randomUUID(),
                UUID.randomUUID(),
                BigDecimal.valueOf(15000));
        paymentService.processPayment(event);

        verify(paymentRepository).save(any(Payment.class));
        verify(paymentEventPublisher).paymentSuccessEvent(any(PaymentCompletedEvent.class));
    }

}
