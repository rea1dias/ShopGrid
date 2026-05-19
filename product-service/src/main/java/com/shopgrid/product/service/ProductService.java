package com.shopgrid.product.service;

import com.shopgrid.product.domain.dto.request.ProductRequest;
import com.shopgrid.product.domain.dto.response.ProductResponse;
import com.shopgrid.product.domain.dto.response.UpdateProductRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ProductService {

    ProductResponse create(ProductRequest request, UUID sellerId);

    Page<ProductResponse> getAll(Pageable pageable);

    ProductResponse findById(UUID id);

    ProductResponse update(UpdateProductRequest request, UUID id, UUID sellerId);
}
