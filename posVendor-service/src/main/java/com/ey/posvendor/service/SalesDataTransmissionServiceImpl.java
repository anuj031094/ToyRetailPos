package com.ey.posvendor.service;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.ey.posvendor.dto.TransmitDataDto;
import com.ey.posvendor.model.TransactionData;
import com.ey.posvendor.repository.TransactionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SalesDataTransmissionServiceImpl implements DataTransmissionService{

    Logger log = LoggerFactory.getLogger(SalesDataTransmissionServiceImpl.class);

    @Autowired
    @Qualifier("sqsClientBuilder")
    AmazonSQS sqsClientBuilder;

    @Autowired
    TransactionRepository transactionRepository;

    @Value("${aws.sqs.queue.url}")
    private String AWS_SQS_QUEUE_URL;

    @Autowired
    private TransmitDataDto transmitDataDto;

    @Autowired
    private ObjectMapper objectMapper;

    @Scheduled(cron = "0 0/1 * * * *")
    public void processData() {
        List<TransactionData> unprocessedData = transactionRepository.findBySentToSalesFalse();

        log.info("Total records to be processed : {}", unprocessedData.size());

        if(!unprocessedData.isEmpty()){
            for(TransactionData data : unprocessedData){
                transmitDataDto.setTransactionId(data.getId());
                transmitDataDto.setCustomerName(data.getCustomerName());
                transmitDataDto.setPaymentMethod(data.getPaymentMethod());
                transmitDataDto.setCreatedAt(data.getCreateAt());
                transmitData(transmitDataDto);
                data.setSentToSales(true);
                transactionRepository.save(data);
            }
        }
    }

    @Override
    public void transmitData(TransmitDataDto transmitDataDto) {
        try {
            log.info("Sending transaction {} to sales team!",transmitDataDto.getTransactionId());
            AmazonSQS sqsClient = sqsClientBuilder;
            SendMessageRequest request = new SendMessageRequest()
                    .withQueueUrl(AWS_SQS_QUEUE_URL)
                    .withMessageBody(objectMapper.writeValueAsString(transmitDataDto))
                    .withDelaySeconds(5);

            sqsClient.sendMessage(request);
            log.info("Successfully sent transaction {} to sales team!",transmitDataDto.getTransactionId());
//                    .getMessageId();
        } catch (Exception e) {
            log.error("Error Occured while sending {} to sales team", transmitDataDto.getTransactionId());
        }
    }
}
