package com.shopgrid.order.service.impl;

import com.shopgrid.order.client.ProductServiceClient;
import com.shopgrid.order.client.UserServiceClient;
import com.shopgrid.order.common.dto.request.OrderRequest;
import com.shopgrid.order.common.dto.request.ProductInfo;
import com.shopgrid.order.common.dto.response.OrderItemResponse;
import com.shopgrid.order.common.dto.response.OrderResponse;
import com.shopgrid.order.common.dto.response.UserResponse;
import com.shopgrid.order.common.enums.CancelReason;
import com.shopgrid.order.common.enums.ChannelType;
import com.shopgrid.order.common.enums.NotificationTemplateType;
import com.shopgrid.order.common.enums.OrderStatus;
import com.shopgrid.order.common.exception.AccessDeniedException;
import com.shopgrid.order.common.exception.NotFoundException;
import com.shopgrid.order.common.exception.OrderAlreadyException;
import com.shopgrid.order.domain.Order;
import com.shopgrid.order.domain.OrderItem;
import com.shopgrid.order.event.NotificationEvent;
import com.shopgrid.order.event.OrderCancelledEvent;
import com.shopgrid.order.event.OrderCreatedEvent;
import com.shopgrid.order.event.OrderItemEvent;
import com.shopgrid.order.kafka.OrderEventPublisher;
import com.shopgrid.order.mapper.OrderMapper;
import com.shopgrid.order.repo.OrderRepository;
import com.shopgrid.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);
    private final OrderRepository orderRepository;
    private final ProductServiceClient productServiceClient;
    private final OrderEventPublisher publisher;
    private final OrderMapper mapper;
    private final UserServiceClient userServiceClient;

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

        log.info("Publishing event {}", event.orderId());
        publisher.publishOrderCreated(event);

        List<OrderItemResponse> responses = items.stream().map(item -> new OrderItemResponse(item.getProductId(), item.getProductName(), item.getPrice(), item.getQuantity())).toList();

        return new OrderResponse(saved.getId(), saved.getUserId(), saved.getStatus(), saved.getTotalPrice(), saved.getCreatedAt(), saved.getUpdatedAt(), responses);
    }

    @Override
    @Transactional
    public void update(UUID orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new NotFoundException(orderId));
        order.setStatus(status);
        order.setUpdatedAt(Instant.now());
        orderRepository.save(order);
        try {
            if (status.equals(OrderStatus.CONFIRMED)) {
                UserResponse user = userServiceClient.getUser(order.getUserId());
                Map<String, Object> context = new HashMap<>();
                context.put("user", user.firstName());
                context.put("email", user.email());

                NotificationEvent event = new NotificationEvent("order-confirmed-" + orderId, order.getUserId(), orderId, ChannelType.PUSH, NotificationTemplateType.ORDER_CONFIRMED, user.email(), order.getTotalPrice(), context);
                publisher.publishSendNotification(event);
            }
        } catch (Exception e) {
            log.warn("Could not fetch user {} for notification, skipping. Reason: {}", order.getUserId(), e.getMessage());
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
        if (OrderStatus.CANCELLED.equals(order.getStatus())) {
            throw new OrderAlreadyException(orderId);
        }
        order.setStatus(OrderStatus.CANCELLED);
        order.setUpdatedAt(Instant.now());
        order.setCancelReason(reason);
        orderRepository.save(order);
        log.info("Saved order: {}", order.getId());
        OrderCancelledEvent event = new OrderCancelledEvent(orderId, userId, order.getItems().stream().map(item -> new OrderItemEvent(item.getProductId(), item.getProductName(), item.getPrice(), item.getQuantity())).toList(), order.getTotalPrice(), order.getCreatedAt());
        publisher.publishCancelOrder(event);

        try {
            UserResponse user = userServiceClient.getUser(order.getUserId());
            NotificationEvent notificationEvent = new NotificationEvent("order-cancelled-" + orderId, order.getUserId(), orderId, ChannelType.PUSH, NotificationTemplateType.ORDER_CANCELLED, user.email(), order.getTotalPrice(), Map.of("user", user.firstName(), "reason", reason.name()));
            publisher.publishSendNotification(notificationEvent);
        } catch (Exception e) {
            log.warn("Could not send notification for orderId: {}", orderId);
        }
        return mapper.toResponse(order);
    }
}
