package com.shopgrid.inventory.controller;

import com.shopgrid.inventory.domain.dto.request.InventoryUpdateRequest;
import com.shopgrid.inventory.domain.dto.response.InventoryResponse;
import com.shopgrid.inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService service;

    @PutMapping("/{productId}/quantity")
    public ResponseEntity<InventoryResponse> addQuantity(@PathVariable("productId") UUID productId,
                                                         @RequestBody InventoryUpdateRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(service.add(productId, request));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<InventoryResponse> get(@PathVariable("productId") UUID productId) {
        return ResponseEntity.status(HttpStatus.OK).body(service.get(productId));
    }

}
