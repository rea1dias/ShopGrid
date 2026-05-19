package com.shopgrid.product.service;

import com.shopgrid.product.domain.dto.request.CategoryRequest;
import com.shopgrid.product.domain.dto.response.CategoryResponse;

public interface CategoryService {

    CategoryResponse create(CategoryRequest request);
}
