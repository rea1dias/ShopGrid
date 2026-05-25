package com.shopgrid.order.service.impl;

import com.shopgrid.order.client.ProductServiceClient;
import com.shopgrid.order.common.dto.request.OrderRequest;
import com.shopgrid.order.common.dto.request.ProductInfo;
import com.shopgrid.order.common.dto.response.OrderItemResponse;
import com.shopgrid.order.common.dto.response.OrderResponse;
import com.shopgrid.order.common.enums.OrderStatus;
import com.shopgrid.order.domain.Order;
import com.shopgrid.order.domain.OrderItem;
import com.shopgrid.order.repo.OrderRepository;
import com.shopgrid.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductServiceClient productServiceClient;

    @Override
    @Transactional
    public OrderResponse create(OrderRequest request, UUID userId) {

        List<OrderItem> items = request.items().stream()
                .map(itemRequest -> {
                    ProductInfo product = productServiceClient.getProductInfo(itemRequest.productId());
                    return OrderItem.builder()
                            .productId(itemRequest.productId())
                            .productName(product.name())
                            .price(product.price())
                            .quantity(itemRequest.quantity())
                            .build();}
                )
                .toList();

        BigDecimal totalPrice = items.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Order order = new Order(
                userId,
                OrderStatus.PENDING,
                totalPrice,
                items,
                Instant.now(),
                Instant.now()
        );
        items.forEach(item -> item.setOrder(order));

        Order saved = orderRepository.save(order);

        List<OrderItemResponse> responses = items.stream()
                .map(item -> new OrderItemResponse(
                                item.getProductId(),
                                item.getProductName(),
                                item.getPrice(),
                                item.getQuantity())
                )
                .toList();

        return new OrderResponse(
                saved.getUserId(),
                saved.getStatus(),
                saved.getTotalPrice(),
                saved.getCreatedAt(),
                saved.getUpdatedAt(),
                responses
        );
    }
}
