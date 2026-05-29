package com.shopgrid.payment.service.impl;

import com.shopgrid.payment.common.enums.FailedReason;
import com.shopgrid.payment.common.enums.PaymentStatus;
import com.shopgrid.payment.domain.entity.Payment;
import com.shopgrid.payment.event.PaymentCompletedEvent;
import com.shopgrid.payment.event.PaymentFailedEvent;
import com.shopgrid.payment.event.StockReservedEvent;
import com.shopgrid.payment.kafka.PaymentEventPublisher;
import com.shopgrid.payment.repo.PaymentRepository;
import com.shopgrid.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentEventPublisher publisher;

    @Override
    public void processPayment(StockReservedEvent event) {
        try {
            Payment payment = new Payment(event.orderId(),event.userId(),event.totalPrice());
            paymentRepository.save(payment);
            publisher.paymentSuccessEvent(new PaymentCompletedEvent(
                    payment.getOrderId(),
                    payment.getUserId()
            ));
            if (payment.getStatus().equals(PaymentStatus.SUCCESS)) {
                publisher.paymentSuccessEvent(new PaymentCompletedEvent(
                        payment.getOrderId(),
                        payment.getUserId()
                ));
            }
            if (payment.getStatus().equals(PaymentStatus.FAILED)) {
                publisher.paymentFailureEvent(new PaymentFailedEvent(
                        payment.getOrderId(),
                        payment.getUserId(),
                        FailedReason.INVALID_CVV.getMessage()
                ));
            }
        } catch (Exception e) {
            log.error("Payment processing failed for orderId: {}, error: {}", event.orderId(), e.getMessage());
        }
    }
}
