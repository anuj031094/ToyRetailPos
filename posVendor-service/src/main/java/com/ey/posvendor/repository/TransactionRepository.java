package com.ey.posvendor.repository;

import com.ey.posvendor.model.TransactionData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<TransactionData, Integer> {
    List<TransactionData> findBySentToSalesFalse();
}