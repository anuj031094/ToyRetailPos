package com.ey.posvendor.service;

import com.ey.posvendor.model.TransactionData;

public interface PosService {
    String saveTransaction(TransactionData transactionData);
}
