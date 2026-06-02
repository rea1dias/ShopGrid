package com.shopgrid.product.service.impl;

import com.shopgrid.product.common.ProductStatus;
import com.shopgrid.product.domain.dto.request.ProductRequest;
import com.shopgrid.product.domain.dto.response.CategoryResponse;
import com.shopgrid.product.domain.dto.response.ProductResponse;
import com.shopgrid.product.domain.entity.Category;
import com.shopgrid.product.domain.entity.Product;
import com.shopgrid.product.event.ProductCreatedEvent;
import com.shopgrid.product.kafka.ProductEventPublisher;
import com.shopgrid.product.mapper.ProductMapper;
import com.shopgrid.product.repo.CategoryRepository;
import com.shopgrid.product.repo.ProductRepository;
import com.shopgrid.product.service.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductEventPublisher publisher;

    @Mock
    private ProductService service;

    @Test
    void shouldCreateProductSuccessfully() {

        UUID categoryId = UUID.randomUUID();
        UUID sellerId = UUID.randomUUID();

        ProductRequest request = new ProductRequest(
                "name",
                "desc",
                BigDecimal.valueOf(150),
                "sku",
                List.of(categoryId)
        );

        Product mapped = new Product();
        when(productMapper.toEntity(request)).thenReturn(mapped);

        Category category = new Category();
        category.setId(categoryId);
        category.setName("name");
        category.setDescription("desc");
        when(categoryRepository.findAllById(request.categoryIds())).thenReturn(List.of(category));

        mapped.setCategories(List.of(category));
        mapped.setSellerId(sellerId);

        Product saved = new Product();
        saved.setId(UUID.randomUUID());
        saved.setName("name");
        saved.setDescription("desc");
        saved.setPrice(BigDecimal.valueOf(150));
        saved.setSku("sku");
        saved.setStatus(ProductStatus.ACTIVE);
        saved.setCategories(List.of(category));
        when(productRepository.save(any(Product.class))).thenReturn(saved);

        ProductResponse response = new ProductResponse(
                saved.getId(),
                "name",
                "desc",
                BigDecimal.valueOf(150),
                "sku",
                ProductStatus.ACTIVE,
                sellerId,
                List.of(new CategoryResponse(categoryId, "name", "desc")),
                Instant.now()
        );
        when(productMapper.toResponse(any(Product.class))).thenReturn(response);

        ProductResponse result = service.create(request, sellerId);

        ProductCreatedEvent event = new ProductCreatedEvent(
                saved.getId(),
                saved.getName(),
                saved.getDescription(),
                saved.getPrice(),
                saved.getSku(),
                saved.getStatus().toString(),
                saved.getCategories().stream().map(Category::getName).toList()
        );

        assertThat(result).isEqualTo(response);

        verify(productMapper).toEntity(request);
        verify(categoryRepository).findAllById(request.categoryIds());
        verify(productRepository).save(saved);
        verify(publisher).publishProductCreated(any(ProductCreatedEvent.class));
        verify(productMapper).toResponse(saved);
    }

}
