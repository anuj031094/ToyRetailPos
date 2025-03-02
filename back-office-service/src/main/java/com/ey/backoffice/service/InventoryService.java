package com.ey.backoffice.service;

import com.ey.backoffice.dto.TransmitDataDto;
import com.ey.backoffice.model.Inventory;
import com.ey.backoffice.repository.InventoryRepository;
import jakarta.persistence.OptimisticLockException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public boolean checkQuantities(List<TransmitDataDto> inventoryRequests) {
//        for (TransmitDataDto eachproduct : inventoryRequests) {
//            Optional<Inventory> inventory = inventoryRepository.findByProductId(eachproduct.getProductId());
//
//            //Checking if we do have product in backoffice system table.
//            if(inventory.isPresent()) {
//
//                //Checking if we do have sufficient stock to fulfil product.
//                if (inventory.get().getStockQuantity() <= 0 || (eachproduct.getQuantity() > inventory.get().getStockQuantity())) {
//                    System.out.println("Invalid quantity for " + eachproduct.getProductId() + ": " + eachproduct.getQuantity());
//                    return false; // Return false if any quantity check fails
//                }
//            }
//            else{
//                return false;
//            }
//        }
//        return true; // Return true if all quantities are valid

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
                System.out.println("Invalid quantity for " + eachRequestedProduct.getProductId() + ": " + eachRequestedProduct.getQuantity());
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

                        LOGGER.info("Updated product id {} successfully", requestedProduct.getProductId());

                }
                return STOCK_INVENTORY_UPDATED;
            } else {
                return INSUFFICIENT_STOCK;
            }
        } catch (OptimisticLockException e) {
            return "InventoryConflict: Another transaction modified the inventory";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    public String rollbackInventory(List<TransmitDataDto> transmitDataDtoList){
        for (TransmitDataDto requestedProduct : transmitDataDtoList) {
            Optional<Inventory> inventory = inventoryRepository.findByProductId(requestedProduct.getProductId());

            //Checking if we do have product in backoffice system table.
            if(inventory.isPresent()) {
                inventory.get().setStockQuantity(inventory.get().getStockQuantity()+requestedProduct.getQuantity());
                inventoryRepository.save(inventory.get());
            }
        }
        return "Rolled back inventory stock!!";
    }
}
