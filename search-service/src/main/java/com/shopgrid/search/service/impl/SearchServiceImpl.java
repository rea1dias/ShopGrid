package com.shopgrid.search.service.impl;

import com.shopgrid.search.document.ProductDocument;
import com.shopgrid.search.event.ProductCreatedEvent;
import com.shopgrid.search.repo.ProductSearchRepository;
import com.shopgrid.search.service.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {

    private final ProductSearchRepository repository;

    @Override
    @Transactional
    public void indexProduct(ProductCreatedEvent event) {
        ProductDocument document = new ProductDocument(
                event.productId().toString(),
                event.name(),
                event.description(),
                event.price(),
                event.sku(),
                event.status(),
                event.categories()
        );
        log.info("Indexing product: {}", document.getId());
        repository.save(document);
    }

    @Override
    public Page<ProductDocument> search(String query, Pageable pageable) {
        return repository.findByNameContainingIgnoreCase(query, pageable);
    }
}
