package com.shopgrid.product.service.impl;

import com.shopgrid.product.domain.dto.request.CategoryRequest;
import com.shopgrid.product.domain.dto.response.CategoryResponse;
import com.shopgrid.product.domain.entity.Category;
import com.shopgrid.product.mapper.CategoryMapper;
import com.shopgrid.product.repo.CategoryRepository;
import com.shopgrid.product.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    @PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
    public CategoryResponse create(CategoryRequest request) {
        Category category = categoryMapper.toEntity(request);
        return categoryMapper.toResponse(categoryRepository.save(category));
    }
}
