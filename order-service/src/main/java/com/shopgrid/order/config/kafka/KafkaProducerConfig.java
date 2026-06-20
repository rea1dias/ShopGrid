package com.shopgrid.order.config.kafka;

import com.shopgrid.order.event.NotificationEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@Configuration
public class KafkaProducerConfig {

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate(ProducerFactory<String, Object> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    @SuppressWarnings("unchecked")
    public KafkaTemplate<String,
            NotificationEvent> notificationEventKafkaTemplate(
            ProducerFactory<String, Object> producerFactory) {
        return new KafkaTemplate<>((ProducerFactory) producerFactory);
    }

    @Bean
    public KafkaTemplate<String, String> stringKafkaTemplate(
            ProducerFactory<String, Object> producerFactory) {
        return new KafkaTemplate<>((ProducerFactory) producerFactory);
    }
}
