package com.ey.backoffice.config;

import com.ey.posvendor.model.TransactionData;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.context.annotation.Bean;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

public class KafkaConsumerConfig {

    @Bean
    public DefaultKafkaConsumerFactory TransactionConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "backoffice-group-id");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), new JsonDeserializer(TransactionData.class));
    }


    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, TransactionData> transactionListener()
    {
        ConcurrentKafkaListenerContainerFactory<String, TransactionData> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(TransactionConsumerFactory());
        return factory;
    }
}
