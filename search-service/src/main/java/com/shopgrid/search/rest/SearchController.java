package com.shopgrid.search.rest;

import com.shopgrid.search.document.ProductDocument;
import com.shopgrid.search.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService service;

    @GetMapping("/products")
    public ResponseEntity<Page<ProductDocument>> search(
            @RequestParam String query,
            Pageable pageable) {
        return ResponseEntity.ok(service.search(query, pageable));
    }


}
