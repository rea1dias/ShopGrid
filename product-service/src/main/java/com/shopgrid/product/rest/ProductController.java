package com.shopgrid.product.rest;

import com.shopgrid.product.common.ProductStatus;
import com.shopgrid.product.domain.dto.request.ProductRequest;
import com.shopgrid.product.domain.dto.response.ProductResponse;
import com.shopgrid.product.domain.dto.response.UpdateProductRequest;
import com.shopgrid.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping("/seller")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ProductResponse> create(
            @RequestBody ProductRequest request,
            @RequestHeader("X-User-Id") UUID sellerId
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.create(request, sellerId));
    }

    @GetMapping
    public ResponseEntity<Page<ProductResponse>> findAll(Pageable pageable) {
        return ResponseEntity.ok(productService.getAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(productService.findById(id));
    }

    @PutMapping("/seller/{id}")
    public ResponseEntity<ProductResponse> update(
            @PathVariable UUID id,
            @RequestBody UpdateProductRequest request,
            @RequestHeader("X-User-Id") UUID sellerId
    ) {
        return ResponseEntity.ok(productService.update(request, id, sellerId));
    }

    @DeleteMapping("/seller/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id,
                                       @RequestHeader("X-User-Id") UUID sellerId) {
        productService.delete(id, sellerId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/filter")
    public ResponseEntity<Page<ProductResponse>> getProducts(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) ProductStatus status,
            Pageable pageable) {
        return ResponseEntity.ok(productService.getProductsByFilter(name, categoryId, minPrice, maxPrice, status, pageable));
    }
}
