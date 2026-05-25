package com.shopgrid.order.controller;

import com.shopgrid.order.common.dto.request.OrderRequest;
import com.shopgrid.order.common.dto.response.OrderResponse;
import com.shopgrid.order.service.OrderService;
import lombok.RequiredArgsConstructor;
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
    public ResponseEntity<OrderResponse> create(@RequestHeader("X-User-id") UUID userId,
                                                @RequestBody OrderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request, userId));
    }


}
