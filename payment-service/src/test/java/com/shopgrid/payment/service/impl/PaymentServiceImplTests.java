package com.shopgrid.payment.service.impl;

import com.shopgrid.payment.domain.entity.Payment;
import com.shopgrid.payment.event.PaymentCompletedEvent;
import com.shopgrid.payment.event.PaymentFailedEvent;
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
import static org.mockito.Mockito.*;

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
        verify(paymentEventPublisher, never()).paymentFailureEvent(any());
    }

    @Test
    public void paymentFailure() {
        StockReservedEvent event = new StockReservedEvent(
                UUID.randomUUID(),
                UUID.randomUUID(),
                BigDecimal.valueOf(2000000));

        paymentService.processPayment(event);

        verify(paymentRepository).save(any(Payment.class));
        verify(paymentEventPublisher).paymentFailureEvent(any(PaymentFailedEvent.class));
        verify(paymentEventPublisher, never()).paymentSuccessEvent(any());
    }

    @Test
    public void duplicatePaymentIsIgnored() {
        StockReservedEvent event = new StockReservedEvent(
                UUID.randomUUID(),
                UUID.randomUUID(),
                BigDecimal.valueOf(15000));

        when(paymentRepository.existsByOrderId(event.orderId())).thenReturn(true);

        paymentService.processPayment(event);

        verify(paymentRepository, never()).save(any(Payment.class));
        verify(paymentEventPublisher, never()).paymentSuccessEvent(any());
        verify(paymentEventPublisher, never()).paymentFailureEvent(any());
    }
}
