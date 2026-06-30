package com.shopgrid.order.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopgrid.order.client.ProductServiceClient;
import com.shopgrid.order.client.UserServiceClient;
import com.shopgrid.order.common.dto.request.*;
import com.shopgrid.order.common.dto.response.*;
import com.shopgrid.order.common.enums.*;
import com.shopgrid.order.common.exception.*;
import com.shopgrid.order.domain.*;
import com.shopgrid.order.event.*;
import com.shopgrid.order.mapper.OrderMapper;
import com.shopgrid.order.mapper.RefundMapper;
import com.shopgrid.order.repo.*;
import com.shopgrid.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);
    private final OrderRepository orderRepository;
    private final ProductServiceClient productServiceClient;
    private final OrderMapper mapper;
    private final UserServiceClient userServiceClient;
    private final ObjectMapper objectMapper;
    private final OutboxEventRepository outboxEventRepository;
    private final OrderStatusHistoryRepository orderStatusHistoryRepository;
    private final RefundMapper refundMapper;
    private final RefundRepository refundRepository;
    private final RefundItemRepository refundItemRepository;

    @Override
    @Transactional
    public OrderResponse create(OrderRequest request, UUID userId) {
        List<OrderItem> items = request.items().stream().map(itemRequest -> {
            ProductInfo product = productServiceClient.getProductInfo(itemRequest.productId());
            return OrderItem.builder().productId(itemRequest.productId()).productName(product.name()).price(product.price()).quantity(itemRequest.quantity()).build();
        }).toList();
        BigDecimal totalPrice = items.stream().map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()))).reduce(BigDecimal.ZERO, BigDecimal::add);
        Order order = new Order(userId, OrderStatus.PENDING, totalPrice, items, Instant.now(), Instant.now());
        items.forEach(item -> item.setOrder(order));
        Order saved = orderRepository.save(order);
        log.info("Order status:{}", saved.getStatus());
        OrderCreatedEvent event = new OrderCreatedEvent(saved.getId(), saved.getUserId(), items.stream().map(item -> new OrderItemEvent(item.getProductId(), item.getProductName(), item.getPrice(), item.getQuantity())).toList(), totalPrice, saved.getCreatedAt());
        try {
            String payload = objectMapper.writeValueAsString(event);
            outboxEventRepository.save(new OutboxEvent("order.created", payload));
        } catch (JsonProcessingException e) {
            throw new EventSerializationException(order.getId());
        }
        List<OrderItemResponse> responses = items.stream().map(item -> new OrderItemResponse(item.getId(), item.getProductId(), item.getProductName(), item.getPrice(), item.getQuantity())).toList();
        return new OrderResponse(saved.getId(), saved.getUserId(), saved.getStatus(), saved.getTotalPrice(), saved.getCreatedAt(), saved.getUpdatedAt(), responses);
    }

    @Override
    @Transactional
    public void update(UUID orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new NotFoundException(orderId));
        if (!order.getStatus().canTransition(status)) {
            throw new InvalidOrderStatusTransitionException("Cannot change order status from " + order.getStatus() + " to " + status);
        }
        OrderStatusHistory history = new OrderStatusHistory(order, order.getStatus(), status);
        orderStatusHistoryRepository.save(history);
        order.setStatus(status);
        order.setUpdatedAt(Instant.now());
        orderRepository.save(order);

        if (status.equals(OrderStatus.RESERVED)) {
            try {
                PaymentRequestedEvent event = new PaymentRequestedEvent(orderId, order.getUserId(), order.getTotalPrice());
                String payload = objectMapper.writeValueAsString(event);
                outboxEventRepository.save(new OutboxEvent("payment.requested", payload));
            } catch (JsonProcessingException e) {
                throw new EventSerializationException(orderId);
            }
        }

        if (status.equals(OrderStatus.CONFIRMED)) {
            try {
                UserResponse user = userServiceClient.getUser(order.getUserId());
                NotificationEvent event = new NotificationEvent("order-confirmed-" + orderId, order.getUserId(), orderId, ChannelType.EMAIL, NotificationTemplateType.ORDER_CONFIRMED, user.email(), order.getTotalPrice(), Map.of("user", user.firstName(), "email", user.email()));
                String payload = objectMapper.writeValueAsString(event);
                outboxEventRepository.save(new OutboxEvent("order.confirmed.notification", payload));
            } catch (Exception e) {
                log.warn("Could not send notification for orderId: {}", orderId);
            }
        }
    }

    @Override
    @Transactional
    public OrderResponse get(UUID orderId, UUID userId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new NotFoundException(orderId));
        if (!order.getUserId().equals(userId)) {
            throw new AccessDeniedException(userId);
        }
        return mapper.toResponse(order);
    }

    @Override
    @Transactional
    public Page<OrderResponse> getMyOrders(UUID userId, Pageable pageable) {
        return orderRepository.findByUserId(userId, pageable).map(mapper::toResponse);
    }

    @Override
    @Transactional
    public OrderResponse cancel(UUID orderId, UUID userId, CancelReason reason) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new NotFoundException(orderId));
        if (!userId.equals(order.getUserId())) {
            throw new AccessDeniedException(userId);
        }

        if (!order.getStatus().canTransition(OrderStatus.CANCELLED)) {
            throw new InvalidOrderStatusTransitionException("Cannot change order status from " + order.getStatus() + " to " + OrderStatus.CANCELLED);
        }
        OrderStatusHistory history = new OrderStatusHistory(order, order.getStatus(), OrderStatus.CANCELLED);
        orderStatusHistoryRepository.save(history);
        order.setStatus(OrderStatus.CANCELLED);
        order.setUpdatedAt(Instant.now());
        order.setCancelReason(reason);
        orderRepository.save(order);
        log.info("Saved order: {}", order.getId());
        OrderCancelledEvent event = new OrderCancelledEvent(orderId, userId, order.getItems().stream().map(item -> new OrderItemEvent(item.getProductId(), item.getProductName(), item.getPrice(), item.getQuantity())).toList(), order.getTotalPrice(), order.getCreatedAt());
        try {
            String payload = objectMapper.writeValueAsString(event);
            outboxEventRepository.save(new OutboxEvent("order.cancelled", payload));
        } catch (JsonProcessingException e) {
            throw new EventSerializationException(order.getId());
        }
        try {
            UserResponse user = userServiceClient.getUser(order.getUserId());
            NotificationEvent notificationEvent = new NotificationEvent("order-cancelled-" + orderId, order.getUserId(), orderId, ChannelType.EMAIL, NotificationTemplateType.ORDER_CANCELLED, user.email(), order.getTotalPrice(), Map.of("user", user.firstName(), "reason", reason.name()));
            String payload = objectMapper.writeValueAsString(notificationEvent);
            outboxEventRepository.save(new OutboxEvent("order.cancelled.notification", payload));
        } catch (Exception e) {
            log.warn("Could not send notification for orderId: {}", orderId);
            throw new EventSerializationException(order.getId());
        }
        return mapper.toResponse(order);
    }

    @Override
    @Transactional
    public OrderResponse cancelItems(UUID orderId, UUID userId, CancelItemRequest request) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new NotFoundException(orderId));
        if (!order.getStatus().equals(OrderStatus.PENDING) && !order.getStatus().equals(OrderStatus.RESERVED)) {
            throw new InvalidOrderStatusTransitionException("This order is can not be cancelled by status");
        }
        List<OrderItem> cancelItems = order.getItems().stream().filter(item -> request.itemIds().contains(item.getId())).toList();
        if (cancelItems.size() == order.getItems().size()) {
            throw new CannotCancelAllItemsException(orderId);
        }
        order.getItems().removeAll(cancelItems);
        order.setUpdatedAt(Instant.now());
        BigDecimal newTotalPrice = order.getItems().stream().map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()))).reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotalPrice(newTotalPrice);
        Order saved = orderRepository.save(order);
        try {
            List<OrderItemEvent> cancelledItems = cancelItems.stream().map(item -> new OrderItemEvent(item.getProductId(), item.getProductName(), item.getPrice(), item.getQuantity())).toList();
            OrderCancelledEvent event = new OrderCancelledEvent(saved.getId(), saved.getUserId(), cancelledItems, saved.getTotalPrice(), saved.getCreatedAt());
            String payload = objectMapper.writeValueAsString(event);
            outboxEventRepository.save(new OutboxEvent("order.cancelled", payload));
        } catch (JsonProcessingException e) {
            throw new EventSerializationException(orderId);
        }
        return mapper.toResponse(order);
    }

    @Override
    @Transactional
    public List<OrderStatusHistoryResponse> getHistory(UUID userId, UUID orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new NotFoundException(orderId));
        if (!order.getUserId().equals(userId)) {
            throw new AccessDeniedException(userId);
        }
        return order.getStatusHistory().stream().map(mapper::toHistoryResponse).toList();
    }

    @Override
    @Transactional
    public OrderResponse retryPayment(UUID orderId, UUID userId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new NotFoundException(orderId));
        if (!order.getUserId().equals(userId)) {
            throw new AccessDeniedException(userId);
        }
        if (!order.getStatus().equals(OrderStatus.PAYMENT_FAILED)) {
            throw new IllegalStateException("This order is can not be retry payment");
        }
        if (order.getPaymentAttempts() >= 3) {
            order.setStatus(OrderStatus.CANCELLED);
            order.setCancelReason(CancelReason.PAYMENT_FAILED);
            order.setUpdatedAt(Instant.now());
            orderRepository.save(order);
            throw new IllegalStateException("You don't have retry attempts");
        }
        order.setStatus(OrderStatus.RESERVED);
        order.setUpdatedAt(Instant.now());
        order.setPaymentAttempts(order.getPaymentAttempts() + 1);
        orderRepository.save(order);
        try {
            PaymentRequestedEvent event = new PaymentRequestedEvent(orderId, order.getUserId(), order.getTotalPrice());
            String payload = objectMapper.writeValueAsString(event);
            outboxEventRepository.save(new OutboxEvent("payment.requested", payload));
        } catch (JsonProcessingException e) {
            throw new EventSerializationException(orderId);
        }
        return mapper.toResponse(order);
    }

    @Override
    @Transactional
    public RefundResponse createRefund(UUID userId, RefundRequest request) {
        Order order = orderRepository.findById(request.orderId()).orElseThrow(() -> new NotFoundException(request.orderId()));
        if (!order.getUserId().equals(userId)) {
            throw new AccessDeniedException(userId);
        }
        if (!order.getStatus().equals(OrderStatus.DELIVERED)) {
            throw new IllegalStateException("This order is can not be refund");
        }
        log.info("deliveredAt: {}", order.getDeliveredAt());
        log.info("isWithin14Days: {}", isWithin14Days(order.getDeliveredAt()));

        if (!isWithin14Days(order.getDeliveredAt())) {
            throw new IllegalStateException("More than 14 days have passed since the order was delivered");
        }
        List<RefundItem> refundItems = order.getItems()
                .stream()
                .filter(orderItem -> request.orderItemIds().contains(orderItem.getId()))
                .map(orderItem -> {
                    RefundItem refundItem = new RefundItem();
                    refundItem.setOrderItemId(orderItem.getId());
                    refundItem.setProductId(orderItem.getProductId());
                    refundItem.setQuantity(orderItem.getQuantity());
                    refundItem.setPrice(orderItem.getPrice());
                    return refundItem;
                })
                .toList();
        if (refundItems.isEmpty()) {
            throw new NotFoundException(order.getId());
        }
        BigDecimal refundAmount = refundItems.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setStatus(OrderStatus.REFUND_REQUESTED);
        order.setUpdatedAt(Instant.now());
        orderRepository.save(order);
        Refund saved = refundRepository.save(new Refund(
                order.getId(),
                userId,
                RefundStatus.REQUESTED,
                refundItems,
                request.reason(),
                refundAmount,
                request.comment()));
        refundItems.forEach(refundItem -> refundItem.setRefund(saved));
        refundItemRepository.saveAll(refundItems);
        return refundMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public RefundResponse findRefund(UUID orderId, UUID userId) {
        Refund refund = refundRepository.findByOrderId(orderId)
                .orElseThrow(() -> new NotFoundException(orderId));
        if (!refund.getUserId().equals(userId)) {
            throw new AccessDeniedException(userId);
        }
        return refundMapper.toResponse(refund);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<RefundResponse> getAllRefunds(Pageable pageable) {
        Page<RefundResponse> page = refundRepository.findAll(pageable)
                .map(refundMapper::toResponse);
        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<RefundResponse> allRefunds(UUID userId) {
        List<Refund> refunds = refundRepository.findByUserId(userId);
        if (refunds.isEmpty()) {
            throw new NotFoundException(userId);
        }
        return refunds
                .stream()
                .map(refundMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public RefundResponse findRequestedRefund(UUID orderId, UUID userId) {
        Refund refund = refundRepository.findByOrderId(orderId)
                .orElseThrow(() -> new NotFoundException(orderId));
        if (!refund.getStatus().equals(RefundStatus.REQUESTED)) {
            throw new InvalidRefundStatusException(orderId);
        }
        return refundMapper.toResponse(refund);
    }

    @Override
    @Transactional
    public RefundResponse rejectRefund(UUID orderId, RejectRequest request) {
        Refund refund = refundRepository.findByOrderId(orderId)
                .orElseThrow(() -> new NotFoundException(orderId));
        if (!refund.getStatus().equals(RefundStatus.REQUESTED)) {
            throw new InvalidRefundStatusException(orderId);
        }
        log.info("Reject Reason" + refund.getRejectionReason());
        refund.setStatus(RefundStatus.REJECTED);
        refund.setRejectionReason(request.rejectReason());
        refund.setResolvedAt(Instant.now());

        Order order = orderRepository.findById(orderId).orElseThrow(() -> new NotFoundException(orderId));
        order.setStatus(OrderStatus.REFUND_REJECTED);
        order.setUpdatedAt(Instant.now());
        orderRepository.save(order);
        return refundMapper.toResponse(refundRepository.save(refund));
    }

    @Override
    @Transactional
    public RefundResponse approveRefund(UUID orderId) {
        Refund refund = refundRepository.findByOrderId(orderId)
                .orElseThrow(() -> new NotFoundException(orderId));
        if (!refund.getStatus().equals(RefundStatus.REQUESTED)) {
            throw new InvalidRefundStatusException(orderId);
        }
        refund.setStatus(RefundStatus.APPROVED);
        refund.setResolvedAt(Instant.now());

        Order order = orderRepository.findById(orderId).orElseThrow(() -> new NotFoundException(orderId));
        order.setStatus(OrderStatus.REFUND_APPROVED);
        order.setUpdatedAt(Instant.now());
        orderRepository.save(order);
        try {
            List<RefundItemEvent> itemEvents = refund.getRefundItems()
                    .stream()
                    .map(item -> new RefundItemEvent(item.getProductId(), item.getQuantity(), item.getPrice()))
                    .toList();
            RefundApprovedEvent event = new RefundApprovedEvent(
                    refund.getId(),
                    refund.getOrderId(),
                    refund.getUserId(),
                    refund.getRefundAmount(),
                    itemEvents);
            String payload = objectMapper.writeValueAsString(event);
            outboxEventRepository.save(new OutboxEvent("refund.approved", payload));
        } catch (JsonProcessingException e) {
            throw new EventSerializationException(order.getId());
        }
        return refundMapper.toResponse(refundRepository.save(refund));
    }

    public static boolean isWithin14Days(Instant deliveredAt) {
        if (deliveredAt == null) {
            return false;
        }
        Instant now = Instant.now();
        Duration duration = Duration.between(deliveredAt, now);
        log.info("duration days: {}", duration.toDays());
        log.info("isNegative: {}", duration.isNegative());
        return !duration.isNegative() && duration.toDays() <= 14;
    }


}
