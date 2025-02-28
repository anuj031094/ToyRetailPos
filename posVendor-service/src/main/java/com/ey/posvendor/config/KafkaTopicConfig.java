package com.ey.posvendor.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic transactionTopic(){
        return new NewTopic("SendTransactions",3,(short)1);
    }
}
