package com.ey.backoffice.service;

import com.ey.posvendor.model.TransactionData;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class TransactionConsumerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionConsumerService.class);

    @KafkaListener(topics = "SendTransactions",
                    groupId = "backoffice-group-id")
    public void consumer(ConsumerRecord<String,TransactionData> data){
//        LOGGER.info("Transaction data received at backoffice for transaction id {}", data.key());

        LOGGER.info("Key: {} | Value: {}", data.key(), data.value());
        LOGGER.info("Partition: {} | Offset: {}", data.partition(), data.offset());

    }

}
