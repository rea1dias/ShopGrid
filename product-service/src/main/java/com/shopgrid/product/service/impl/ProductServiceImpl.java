package com.shopgrid.product.service.impl;

import com.shopgrid.product.domain.dto.request.ProductRequest;
import com.shopgrid.product.domain.dto.response.ProductResponse;
import com.shopgrid.product.domain.entity.Category;
import com.shopgrid.product.domain.entity.Product;
import com.shopgrid.product.exception.NotFoundException;
import com.shopgrid.product.mapper.ProductMapper;
import com.shopgrid.product.repo.CategoryRepository;
import com.shopgrid.product.repo.ProductRepository;
import com.shopgrid.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CategoryRepository categoryRepository;


    @Override
    @PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
    public ProductResponse create(ProductRequest request, UUID sellerId) {
        log.info("SellerId: {}", sellerId);
        Product product = productMapper.toEntity(request);
        List<Category> categories = categoryRepository.findAllById(request.categoryIds());
        if (categories.size() != request.categoryIds().size()) {
            throw new NotFoundException("One or more categories not found");
        }
        product.setCategories(categories);
        product.setSellerId(sellerId);
        log.info("Product: {}", product);
        return productMapper.toResponse(productRepository.save(product));
    }
}
