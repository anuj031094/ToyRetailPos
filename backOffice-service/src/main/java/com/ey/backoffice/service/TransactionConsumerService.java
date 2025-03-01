package com.ey.backoffice.service;

import com.ey.backoffice.model.Inventory;
import com.ey.backoffice.model.InventoryTransactionLog;
import com.ey.backoffice.repository.InventoryLogRepository;
import com.ey.backoffice.repository.InventoryRepository;
import com.ey.posvendor.dto.TransmitDataDto;
import com.ey.posvendor.model.TransactionData;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Optional;

@Service
public class TransactionConsumerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionConsumerService.class);

    @Autowired
    InventoryRepository inventoryRepository;

    @Autowired
    InventoryLogRepository inventoryLogRepository;

    @Autowired
    public KafkaTemplate<String, TransmitDataDto> kafkaTemplate;


    @KafkaListener(topics = "SendTransactions",
                    groupId = "backoffice-group-id",
            clientIdPrefix = "json",
            containerFactory ="transactionListener")
    public void consumer(ConsumerRecord<String, TransmitDataDto> data, @Payload TransmitDataDto transmitDataDto){
        LOGGER.info("Transaction data received at backoffice for transaction id {}", transmitDataDto.getTransactionId());

        LOGGER.info("Key: {} | Value: {}", data.key(), data.value());
        LOGGER.info("Partition: {} | Offset: {}", data.partition(), data.offset());

        try {
            Optional<Inventory> product = inventoryRepository.findByProductId(transmitDataDto.getProductId());

            if (product.isPresent()) {
                Inventory inventory = product.get();
                if (inventory.getStockQuantity() >= transmitDataDto.getQuantity()) {

                    // Updating inventory_transaction_log to keep records of all transaction processed in inventory
                    InventoryTransactionLog log = new InventoryTransactionLog();
                    log.setTransactionId(transmitDataDto.getTransactionId());
                    log.setProductId(transmitDataDto.getProductId());
                    log.setQuantityPurchased(transmitDataDto.getQuantity());
                    log.setLastUpdated(new Timestamp(System.currentTimeMillis()));
                    inventoryLogRepository.save(log);

                    // Updating stock quantity of product after transaction
                    inventory.setStockQuantity(inventory.getStockQuantity() - transmitDataDto.getQuantity());
                    inventoryRepository.save(inventory);
                } else {
                    LOGGER.info("OUT OF STOCK FOR PRODUCT : {}", inventory.getProductId());
                    throw new RuntimeException();
                }
            } else {
                // Handle case where the productId doesn't exist
                LOGGER.info("Product with id " + data.value().getProductId() + " not found.");
                throw new RuntimeException();
            }
        } catch (Exception e) {
            LOGGER.info("EXCEPTION OCCURED FOR TRANSACTION ID : {}", transmitDataDto.getTransactionId());
            reverseInventoryUpdate(transmitDataDto);
            reverseTransactionUpdate(transmitDataDto);

        }
    }

    //Compensation handler : If inventory update failed, reverse all transaction
    public void reverseInventoryUpdate(TransmitDataDto transmitDataDto){

        LOGGER.info("DELETING RECORDS FROM INVENTORY AND UPDATING : {}", transmitDataDto.getTransactionId());
        Optional<InventoryTransactionLog> inventoryTransactionLog = inventoryLogRepository.findByTransactionId(transmitDataDto.getTransactionId());

        Optional<Inventory> inventory = inventoryRepository.findByProductId(inventoryTransactionLog.get().getProductId());

        inventoryTransactionLog.ifPresent(transactionLog -> inventory.get().setStockQuantity(inventory.get().getStockQuantity() + transactionLog.getQuantityPurchased()));

        inventoryRepository.save(inventory.get());
        inventoryLogRepository.delete(inventoryTransactionLog.get());
    }

    //Compensation handler : If inventory update failed, reverse all transaction
    public void reverseTransactionUpdate(TransmitDataDto transmitDataDto){

        LOGGER.info("SENDING FAILED RESPONSE FOR TRANSACTION ID: {}", transmitDataDto.getTransactionId());
        Message<TransmitDataDto> message = MessageBuilder.withPayload(transmitDataDto)
                .setHeader(KafkaHeaders.TOPIC, "UndoFailedTransaction")
                .build();
        kafkaTemplate.send(message);
    }
}
