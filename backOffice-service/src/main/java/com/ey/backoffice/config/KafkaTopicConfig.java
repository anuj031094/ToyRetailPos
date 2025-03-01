package com.ey.backoffice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic transactionTopic(){
        return new NewTopic("UndoFailedTransaction",3,(short)1);
    }
}
