package com.shopgrid.order.controller;

import com.shopgrid.order.common.dto.request.CancelRequest;
import com.shopgrid.order.common.dto.request.OrderRequest;
import com.shopgrid.order.common.dto.response.OrderResponse;
import com.shopgrid.order.common.enums.CancelReason;
import com.shopgrid.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService service;

    @PostMapping
    public ResponseEntity<OrderResponse> create(@RequestHeader("X-User-Id") UUID userId,
                                                @RequestBody OrderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request, userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> get(@RequestHeader("X-User-Id") UUID userId,
                                             @PathVariable UUID id) {
        return ResponseEntity.status(HttpStatus.OK).body(service.get(id, userId));
    }

    @GetMapping
    public ResponseEntity<Page<OrderResponse>> my(@RequestHeader("X-User-Id") UUID userId,
                                                  Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(service.getMyOrders(userId, pageable));
    }

    @PostMapping("/cancel/{orderId}")
    public ResponseEntity<OrderResponse> cancel(@PathVariable UUID orderId,
                                                @RequestHeader("X-User-Id") UUID userId,
                                                @RequestBody CancelRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(service.cancel(orderId, userId, request.cancelReason()));
    }



}
