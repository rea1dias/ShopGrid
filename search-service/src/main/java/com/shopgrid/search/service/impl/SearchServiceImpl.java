package com.shopgrid.search.service.impl;

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import com.shopgrid.search.document.ProductDocument;
import com.shopgrid.search.event.ProductCreatedEvent;
import com.shopgrid.search.repo.ProductSearchRepository;
import org.springframework.data.elasticsearch.core.SearchHit;
import com.shopgrid.search.service.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {

    private final ProductSearchRepository repository;
    private final ElasticsearchOperations elasticsearchOperations;

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

    @Override
    public Page<ProductDocument> search(String query,
                                        BigDecimal minPrice,
                                        BigDecimal maxPrice,
                                        String category,
                                        Pageable pageable) {

        BoolQuery.Builder boolQuery = new BoolQuery.Builder();

        if (query != null && !query.isBlank()) {
            boolQuery.should(s -> s.multiMatch(m -> m
                    .query(query)
                    .fields("name", "description")
                    .fuzziness("AUTO")));
            boolQuery.minimumShouldMatch("1");
        }

        if (minPrice != null) {
            final double min = minPrice.doubleValue();
            boolQuery.filter(f -> f.range(r -> r
                    .number(n -> n.field("price").gte(min))));
        }
        if (maxPrice != null) {
            final double max = maxPrice.doubleValue();
            boolQuery.filter(f -> f.range(r -> r
                    .number(n -> n.field("price").lte(max))));
        }

        if (category != null && !category.isBlank()) {
            boolQuery.filter(f -> f.term(t -> t
                    .field("categories")
                    .value(category)));
        }

        NativeQuery nativeQuery = NativeQuery.builder()
                .withQuery(q -> q.bool(boolQuery.build()))
                .withPageable(pageable).build();


        SearchHits<ProductDocument> hits = elasticsearchOperations.search(nativeQuery, ProductDocument.class);

        List<ProductDocument> documents = hits.getSearchHits()
                .stream()
                .map(SearchHit::getContent)
                .toList();

        return new PageImpl<>(documents, pageable, hits.getTotalHits());
    }
}
