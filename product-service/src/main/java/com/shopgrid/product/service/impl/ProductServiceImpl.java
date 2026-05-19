package com.shopgrid.product.service.impl;

import com.shopgrid.product.domain.dto.request.ProductRequest;
import com.shopgrid.product.domain.dto.response.ProductResponse;
import com.shopgrid.product.domain.dto.response.UpdateProductRequest;
import com.shopgrid.product.domain.entity.Category;
import com.shopgrid.product.domain.entity.Product;
import com.shopgrid.product.exception.NotFoundException;
import com.shopgrid.product.mapper.ProductMapper;
import com.shopgrid.product.repo.CategoryRepository;
import com.shopgrid.product.repo.ProductRepository;
import com.shopgrid.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Override
    public Page<ProductResponse> getAll(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(productMapper::toResponse);
    }

    @Override
    public ProductResponse findById(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found"));
        return productMapper.toResponse(product);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('SELLER') or hasRole('ADMIN')")
    public ProductResponse update(UpdateProductRequest request, UUID id, UUID sellerId) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found"));
        if (!product.getSellerId().equals(sellerId)) {
            throw new AccessDeniedException("Only product owner can update product");
        }
        List<Category> categories = categoryRepository.findAllById(request.categoryIds());
        if (categories.size() != request.categoryIds().size()) {
            throw new NotFoundException("One or more categories not found");
        }
        productMapper.updateEntity(request, product);
        product.setCategories(categories);
        return productMapper.toResponse(productRepository.save(product));
    }
}
