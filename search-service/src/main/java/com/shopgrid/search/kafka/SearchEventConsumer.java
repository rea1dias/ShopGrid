package com.shopgrid.search.kafka;

import com.shopgrid.search.event.ProductCreatedEvent;
import com.shopgrid.search.service.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SearchEventConsumer {

    private final SearchService service;

    @KafkaListener(topics = "product.created", groupId = "search-service",
            properties = {"spring.json.value.default.type=com.shopgrid.search.event.ProductCreatedEvent"})
    public void handleProductCreated(ProductCreatedEvent event) {
        log.info("Received product created event: {}", event.productId());
        service.indexProduct(event);
    }

}
