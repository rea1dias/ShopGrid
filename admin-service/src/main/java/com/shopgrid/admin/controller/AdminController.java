package com.shopgrid.admin.controller;

import com.shopgrid.admin.domain.dto.request.RejectRequest;
import com.shopgrid.admin.domain.dto.response.PageResponse;
import com.shopgrid.admin.domain.dto.response.RefundResponse;
import com.shopgrid.admin.domain.dto.response.UserResponse;
import com.shopgrid.admin.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService service;

    @PostMapping("/block/{id}")
    public ResponseEntity<Void> block(@PathVariable UUID id, @RequestHeader("Authorization") String token) {
        service.blockUser(id, token);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> get(@PathVariable UUID id, @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok().body(service.get(id, token));
    }

    @GetMapping("/refunds")
    public ResponseEntity<PageResponse<RefundResponse>> getAllResponses(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok().body(service.getAllRefunds(token));
    }

    @PostMapping("/refunds/reject/{orderId}")
    public ResponseEntity<RefundResponse> reject(@PathVariable UUID orderId, @RequestHeader("Authorization") String token, @RequestBody RejectRequest request) {
        return ResponseEntity.ok().body(service.rejectRefund(orderId, token, request));
    }

    @PostMapping("/refunds/approve/{orderId}")
    public ResponseEntity<RefundResponse> approve(@PathVariable UUID orderId, @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok().body(service.approveRefund(orderId, token));
    }
}
