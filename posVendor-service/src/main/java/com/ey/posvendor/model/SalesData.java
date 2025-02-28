package com.ey.posvendor.model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import java.math.BigDecimal;
import java.util.UUID;

public class SalesData {

    @Id
    @GeneratedValue
    private UUID salesId;
    private UUID transactionId;
    private String storeId;
    private BigDecimal totalAmount;
    private int numberOfItems;

}
