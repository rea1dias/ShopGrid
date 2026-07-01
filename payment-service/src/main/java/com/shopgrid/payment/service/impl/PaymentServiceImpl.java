package com.shopgrid.payment.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopgrid.payment.common.enums.FailedReason;
import com.shopgrid.payment.common.enums.PaymentStatus;
import com.shopgrid.payment.domain.entity.Payment;
import com.shopgrid.payment.event.*;
import com.shopgrid.payment.repo.OutboxEventRepository;
import com.shopgrid.payment.repo.PaymentRepository;
import com.shopgrid.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public void processPayment(PaymentRequestedEvent event) {
        Optional<Payment> existing = paymentRepository.findByOrderId(event.orderId());
        if (existing.isPresent() && existing.get().getStatus().equals(PaymentStatus.SUCCESS)) {
            log.warn("Payment already SUCCESS for orderId: {}, skipping", event.orderId());
            return;
        }
        Payment payment = new Payment(event.orderId(), event.userId(), event.totalPrice());
        paymentRepository.save(payment);
        try {
            if (payment.getStatus().equals(PaymentStatus.SUCCESS)) {
                PaymentCompletedEvent completedEvent = new PaymentCompletedEvent(event.orderId(), event.userId());
                String payload = objectMapper.writeValueAsString(completedEvent);
                outboxEventRepository.save(new OutboxEvent("payment.completed", payload));
            } else if (payment.getStatus().equals(PaymentStatus.FAILED)) {
                PaymentFailedEvent failedEvent = new PaymentFailedEvent(event.orderId(), event.userId(), FailedReason.INVALID_CVV.getMessage());
                String payload = objectMapper.writeValueAsString(failedEvent);
                outboxEventRepository.save(new OutboxEvent("payment.failed", payload));
            }
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize payment event", e);
        }
    }

    @Override
    @Transactional
    public void refundPayment(RefundApprovedEvent event) {
        Payment payment = paymentRepository.findByOrderIdAndStatus(event.orderId(), PaymentStatus.SUCCESS)
                .orElseThrow(() -> new IllegalStateException("Payment not found"));
        payment.setStatus(PaymentStatus.REFUNDED);
        paymentRepository.save(payment);
        try {
            PaymentRefundedEvent paymentRefundedEvent = new PaymentRefundedEvent(event.refundId(), event.orderId(), event.userId(), event.refundAmount());
            String payload = objectMapper.writeValueAsString(paymentRefundedEvent);
            outboxEventRepository.save(new OutboxEvent("payment.refunded", payload));
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize payment event", e);
        }
    }
}
