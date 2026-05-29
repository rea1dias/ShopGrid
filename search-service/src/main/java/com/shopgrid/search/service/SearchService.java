package com.shopgrid.search.service;

import com.shopgrid.search.document.ProductDocument;
import com.shopgrid.search.event.ProductCreatedEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SearchService {

    void indexProduct(ProductCreatedEvent event);
    Page<ProductDocument> search(String query, Pageable pageable);
}
