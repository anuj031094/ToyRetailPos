package com.ey.sales.service;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.ey.sales.dto.TransmitDataDto;
import com.ey.sales.model.SalesData;
import com.ey.sales.repository.SalesRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
public class fetchTransactionData {


    Logger LOGGER = LoggerFactory.getLogger(fetchTransactionData.class);

    @Autowired
    private AmazonSQS amazonSQS;

    @Value("${aws.sqs.queue.url}")
    private String AWS_SQS_QUEUE_URL;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SalesRepository salesRepository;

    @Scheduled(cron = "0 0/1 * * * *")
    public void fetchMessagesFromSQS() throws JsonProcessingException {
        // Create a receive message request
        LOGGER.info("Fetching messages");
        ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(AWS_SQS_QUEUE_URL)
                .withMaxNumberOfMessages(10) // Max number of messages to retrieve at once
                .withWaitTimeSeconds(20); // Long polling for 20 seconds

        // Fetch messages from SQS
        List<Message> messages = amazonSQS.receiveMessage(receiveMessageRequest).getMessages();

        LOGGER.info("TOTAL MESSAGE FETCHED : {}",messages.size());
        // Process the received messages
        for (Message message : messages) {
            // Print out the body of each message
//            System.out.println("Message ID: " + message.getMessageId());
//            System.out.println("Message Body: " + message.getBody());




            TransmitDataDto transaction = objectMapper.readValue(message.getBody(), TransmitDataDto.class);
            SalesData salesData = new SalesData();
            salesData.setTransactionId(transaction.getTransactionId());
            salesData.setNumberOfItems(transaction.getQuantity());
            salesData.setCustomerName(transaction.getCustomerName());
            salesData.setTransactionDate(transaction.getCreatedAt());
            salesData.setPaymentMethod(transaction.getPaymentMethod());
            salesData.setTotalAmount(transaction.getTotalAmount());
            salesData.setCreatedOn(new Timestamp(System.currentTimeMillis()));
            salesRepository.save(salesData);
            LOGGER.info("Transaction ID : {}",transaction.getTransactionId());
            // After processing, delete the message from the queue
            amazonSQS.deleteMessage(AWS_SQS_QUEUE_URL, message.getReceiptHandle());
        }
    }
}
