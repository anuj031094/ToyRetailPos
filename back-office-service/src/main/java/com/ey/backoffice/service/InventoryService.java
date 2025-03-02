package com.ey.backoffice.service;

import com.ey.backoffice.dto.TransmitDataDto;
import com.ey.backoffice.model.Inventory;
import com.ey.backoffice.model.InventoryTransactionLog;
import com.ey.backoffice.repository.InventoryLogRepository;
import com.ey.backoffice.repository.InventoryRepository;
import jakarta.persistence.OptimisticLockException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.ey.backoffice.constants.BackOfficeConstants.*;

@Service
public class InventoryService {

    private static final Logger LOGGER = LoggerFactory.getLogger(InventoryService.class);

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private InventoryLogRepository inventoryLogRepository;

    public boolean checkQuantities(List<TransmitDataDto> inventoryRequests) {

        for (TransmitDataDto eachRequestedProduct : inventoryRequests) {
            Optional<Inventory> inventoryOpt = inventoryRepository.findByProductId(eachRequestedProduct.getProductId());

            // Check if the inventory is present
            if (inventoryOpt.isEmpty()) {
                return false; // Return false if inventory is not found
            }

            // Retrieve the inventory object
            Inventory inventory = inventoryOpt.get();

            // Check stock quantity availability
            if (inventory.getStockQuantity() <= 0 || eachRequestedProduct.getQuantity() > inventory.getStockQuantity()) {
                LOGGER.info("Invalid quantity for product {}", eachRequestedProduct.getProductId());
                return false; // Return false if stock quantity is insufficient
            }
        }

        return true; // Return true if all checks pass

    }


    @Transactional
    public String updateInventory(List<TransmitDataDto> inventoryRequests) {
        try {
            if (checkQuantities(inventoryRequests)) {

                // Updating each product in a transaction.
                for (TransmitDataDto requestedProduct : inventoryRequests) {
                    LOGGER.info("Updating inventory product id {} successfully", requestedProduct.getProductId());

                    Optional<Inventory> inventory = inventoryRepository.findByProductId(requestedProduct.getProductId());

                        inventory.get().setStockQuantity(inventory.get().getStockQuantity() - requestedProduct.getQuantity());

                        inventoryRepository.save(inventory.get());
                        saveInventoryLogs(requestedProduct);

                        LOGGER.info("Updated product id {} successfully", requestedProduct.getProductId());

                }
                return STOCK_INVENTORY_UPDATED;
            } else {
                return INSUFFICIENT_STOCK;
            }
        } catch (Exception e) {
            rollbackInventory(inventoryRequests);
            return "Error: " + e.getMessage();
        }
    }

    public void saveInventoryLogs(TransmitDataDto requestedProduct){
        InventoryTransactionLog inventoryTransactionLog = new InventoryTransactionLog();
        inventoryTransactionLog.setTransactionId(requestedProduct.getTransactionId());
        inventoryTransactionLog.setProductId(requestedProduct.getProductId());
        inventoryTransactionLog.setQuantityPurchased(requestedProduct.getQuantity());
        inventoryTransactionLog.setLastUpdated(new Timestamp(System.currentTimeMillis()));
        inventoryLogRepository.save(inventoryTransactionLog);
    }

    public void rollbackInventory(List<TransmitDataDto> transmitDataDtoList){
        for (TransmitDataDto requestedProduct : transmitDataDtoList) {
            LOGGER.info("ROLLING BACK INVENTORY FOR TRANSACTION : {}", requestedProduct.getTransactionId());

            Optional<Inventory> inventory = inventoryRepository.findByProductId(requestedProduct.getProductId());
            Optional<InventoryTransactionLog> inventoryLog = inventoryLogRepository.findByTransactionId(requestedProduct.getTransactionId());

            //Rolling back transaction in inventory transaction table.
            inventoryLog.ifPresent(inventoryTransactionLog -> inventoryLogRepository.delete(inventoryTransactionLog));

            //Checking if we do have product in backoffice system table.
            if(inventory.isPresent()) {
                inventory.get().setStockQuantity(inventory.get().getStockQuantity()+requestedProduct.getQuantity());
                inventoryRepository.save(inventory.get());
            }
            LOGGER.info("ROLLED BACK INVENTORY FOR TRANSACTION : {}", requestedProduct.getTransactionId());
        }
    }
}
