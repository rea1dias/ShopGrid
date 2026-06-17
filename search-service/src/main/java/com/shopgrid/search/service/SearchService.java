package com.shopgrid.search.service;

import com.shopgrid.search.document.ProductDocument;
import com.shopgrid.search.event.ProductCreatedEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface SearchService {

    void indexProduct(ProductCreatedEvent event);

    Page<ProductDocument> search(String query, Pageable pageable);

    Page<ProductDocument> search(String query, BigDecimal minPrice, BigDecimal maxPrice, String category, Pageable pageable);
}
