package com.shopgrid.product.service.impl;

import com.shopgrid.product.common.ProductStatus;
import com.shopgrid.product.domain.dto.request.ProductRequest;
import com.shopgrid.product.domain.dto.response.CategoryResponse;
import com.shopgrid.product.domain.dto.response.ProductResponse;
import com.shopgrid.product.domain.entity.Category;
import com.shopgrid.product.domain.entity.Product;
import com.shopgrid.product.event.ProductCreatedEvent;
import com.shopgrid.product.exception.NotFoundException;
import com.shopgrid.product.kafka.ProductEventPublisher;
import com.shopgrid.product.mapper.ProductMapper;
import com.shopgrid.product.repo.CategoryRepository;
import com.shopgrid.product.repo.ProductRepository;
import com.shopgrid.product.service.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductEventPublisher publisher;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductServiceImpl productService;

    @Test
    public void createProductSuccessfully() {

        UUID sellerId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();
        ProductRequest request = new ProductRequest(
                "name",
                "description",
                BigDecimal.valueOf(15000),
                "skuuuu",
                List.of(categoryId)
        );
        Category category = new Category();
        category.setId(UUID.randomUUID());
        category.setName("Name");
        category.setDescription("Description");
        when(categoryRepository.findAllById(request.categoryIds())).thenReturn(List.of(category));
        Product product = new Product();
        product.setId(UUID.randomUUID());
        product.setName("Name");
        product.setDescription("Description");
        product.setPrice(BigDecimal.valueOf(7500));
        product.setSku("hello");
        product.setSellerId(sellerId);
        product.setCreatedAt(Instant.now());
        product.setCategories(List.of(category));
        when(productMapper.toEntity(request)).thenReturn(product);
        Product saved = new Product();
        saved.setId(UUID.randomUUID());
        saved.setName("Name");
        saved.setDescription("Description");
        saved.setPrice(BigDecimal.valueOf(15000));
        saved.setStatus(ProductStatus.ACTIVE);
        saved.setSku("hello");
        saved.setCategories(List.of(category));
        when(productRepository.save(any(Product.class))).thenReturn(saved);
        productService.create(request, sellerId);
        verify(publisher).publishProductCreated(any());
    }

    @Test
    public void createProductFailed() {

        UUID sellerId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();
        ProductRequest request = new ProductRequest(
                "name",
                "description",
                BigDecimal.valueOf(15000),
                "skuuuu",
                List.of(categoryId)
        );

        when(categoryRepository.findAllById(List.of(categoryId))).thenReturn(Collections.emptyList());
        assertThrows(
                NotFoundException.class,
                () -> productService.create(request, sellerId)
        );

        verify(productRepository, never()).save(any());
        verify(publisher, never()).publishProductCreated(any());
    }


}
