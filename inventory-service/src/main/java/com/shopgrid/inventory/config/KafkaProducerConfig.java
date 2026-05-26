package com.shopgrid.inventory.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@Configuration
public class KafkaProducerConfig {

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate(ProducerFactory<String, Object> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    public NewTopic stockReservedTopic() {
        return TopicBuilder.name("stock.reserved")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic stockFailedTopic() {
        return TopicBuilder.name("stock.failed")
                .partitions(1)
                .replicas(1)
                .build();
    }
}
