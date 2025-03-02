package com.ey.backoffice.controller;

import com.ey.backoffice.dto.TransmitDataDto;
import com.ey.backoffice.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

    @Autowired
    InventoryService inventoryService;


    @PostMapping("/update")
    public ResponseEntity<String> reserveInventory(@RequestBody List<TransmitDataDto> request) {
        try {
            String response = inventoryService.updateInventory(request);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }
}
