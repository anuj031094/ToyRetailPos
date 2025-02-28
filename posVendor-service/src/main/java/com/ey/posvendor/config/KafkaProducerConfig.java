package com.ey.posvendor.config;

import com.ey.posvendor.model.TransactionData;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaProducerConfig {

    @Bean
    public ProducerFactory<String, TransactionData> TransactionProducerFactroy() {
        // Create producer configurations using values from application.properties
        Map<String, Object> producerProps = new HashMap<>();

        // Bootstrap server URL
        producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");

        // Key and value serializers
        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        // Configure batch size, linger.ms, and compression type
//        producerProps.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);  // 16 KB batch size
//        producerProps.put(ProducerConfig.LINGER_MS_CONFIG, 10);      // 10 ms linger time
//        producerProps.put(ProducerConfig.ACKS_CONFIG, "all");        // All replicas acknowledgment
//        producerProps.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "gzip"); // Message compression

        // Create and return the KafkaTemplate
        return new DefaultKafkaProducerFactory<>(producerProps);
    }
    @Bean
    public KafkaTemplate<String, TransactionData> TransactionKafkaTemplate()
    {
        return new KafkaTemplate<>(TransactionProducerFactroy());
    }
}