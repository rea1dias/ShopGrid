package com.shopgrid.product.service;

import com.shopgrid.product.domain.dto.request.ProductRequest;
import com.shopgrid.product.domain.dto.response.ProductResponse;

import java.util.UUID;

public interface ProductService {

    ProductResponse create(ProductRequest request, UUID sellerId);
}
