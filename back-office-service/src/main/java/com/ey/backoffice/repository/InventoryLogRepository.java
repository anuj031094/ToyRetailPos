package com.ey.backoffice.repository;

import com.ey.backoffice.model.Inventory;
import com.ey.backoffice.model.InventoryTransactionLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InventoryLogRepository extends JpaRepository<InventoryTransactionLog, Integer> {

    Optional<InventoryTransactionLog> findByTransactionId(Integer transactionId);
}
