package com.ey.posvendor.controller;

import com.ey.posvendor.model.TransactionData;
import com.ey.posvendor.service.PosServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pos")
public class PosTransactionController {

    private final PosServiceImpl posServiceImpl;

    public PosTransactionController(PosServiceImpl posServiceImpl) {
        this.posServiceImpl = posServiceImpl;
    }

    @PostMapping("/transactions")
    public ResponseEntity<String> receiveTransactionData(@RequestBody TransactionData transactionData) {
//        try {
            // Pass the data to the service layer to be processed and stored.
            posServiceImpl.saveTransaction(transactionData);
            return ResponseEntity.status(HttpStatus.CREATED).body("Transaction Data Received Successfully");
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing transaction data");
//        }
    }
}
