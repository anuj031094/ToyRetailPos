package com.ey.posvendor.service;

import com.ey.posvendor.dto.TransmitDataDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BackOfficeDataTransmissionServiceImpl implements DataTransmissionService{

    Logger log = LoggerFactory.getLogger(BackOfficeDataTransmissionServiceImpl.class);
    @Autowired
    public KafkaTemplate<String, TransmitDataDto> kafkaTemplate;

    public BackOfficeDataTransmissionServiceImpl(KafkaTemplate<String, TransmitDataDto> kafkaTemplate){
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void transmitData(TransmitDataDto transmitDataDto) {
        //Kafka logic
        log.info("Sending data to BackTeam {} : ",transmitDataDto.toString());
        Message<TransmitDataDto> message = MessageBuilder.withPayload(transmitDataDto)
                .setHeader(KafkaHeaders.TOPIC, "SendTransactions")
                .build();

        kafkaTemplate.send(message).whenComplete((sendResult, throwable) ->{
            if(throwable!=null){
                onFailure();
            }
            else {
                onSuccess(sendResult);
            }
        });
    }

    private void onSuccess(SendResult<String, TransmitDataDto> sendResult) {
//        System.out.println("Topic : " + sendResult.getRecordMetadata().topic());
//
//        System.out.println("Partition : " + sendResult.getRecordMetadata().partition());
//
//        System.out.println("Offset : " + sendResult.getRecordMetadata().offset());
//
//        System.out.println("Value : " + sendResult.getRecordMetadata().toString());

    }

    private void onFailure() {

        System.out.println("FIXXXXX ITTTTTT!!!!!!!");
    }
}
