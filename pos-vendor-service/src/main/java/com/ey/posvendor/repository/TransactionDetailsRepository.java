package com.ey.posvendor.repository;

import com.ey.posvendor.model.TransactionData;
import com.ey.posvendor.model.TransactionDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionDetailsRepository extends JpaRepository<TransactionDetails, Integer> {
    List<TransactionDetails> findBytransactionData(TransactionData transactionData);
}