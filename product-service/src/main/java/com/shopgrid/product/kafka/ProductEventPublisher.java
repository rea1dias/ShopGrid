package com.shopgrid.product.kafka;

import com.shopgrid.product.event.ProductCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishProductCreated(ProductCreatedEvent event) {
        log.info("Publishing product.created for productId: {}", event.productId());
        kafkaTemplate.send("product.created", event.productId().toString(), event);
    }
}
