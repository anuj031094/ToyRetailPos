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
import java.util.Optional;

import static com.ey.backoffice.constants.BackOfficeConstants.*;

@Service
public class InventoryService {

    private static final Logger LOGGER = LoggerFactory.getLogger(InventoryService.class);

    @Autowired
    private InventoryRepository inventoryRepository;

    public boolean checkQuantities(List<TransmitDataDto> inventoryRequests) {
        for (TransmitDataDto eachproduct : inventoryRequests) {
            Optional<Inventory> inventory = inventoryRepository.findByProductId(eachproduct.getProductId());

            //Checking if we do have product in backoffice system table.
            if(inventory.isPresent()) {

                //Checking if we do have sufficient stock to fulfil product.
                if (inventory.get().getStockQuantity() <= 0 || (eachproduct.getQuantity() > inventory.get().getStockQuantity())) {
                    System.out.println("Invalid quantity for " + eachproduct.getProductId() + ": " + eachproduct.getQuantity());
                    return false; // Return false if any quantity check fails
                }
            }
            else{
                return false;
            }
        }
        return true; // Return true if all quantities are valid
    }

    @Transactional
    public String updateInventory(List<TransmitDataDto> inventoryRequests) {
        try {
            if (checkQuantities(inventoryRequests)) {

                // Updating each product in a transaction.
                for (TransmitDataDto eachproduct : inventoryRequests) {
                    LOGGER.info("Updating inventory product id {} successfully", eachproduct.getProductId());

                    Optional<Inventory> inventory = inventoryRepository.findByProductId(eachproduct.getProductId());

                        inventory.get().setStockQuantity(inventory.get().getStockQuantity() - eachproduct.getQuantity());
                        inventoryRepository.save(inventory.get());

                        LOGGER.info("Updated product id {} successfully", eachproduct.getProductId());

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
}
