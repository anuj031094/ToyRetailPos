package com.ey.sales.model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;

public class SalesData {

    @Id
    @GeneratedValue
    private int salesId;
    private int transactionId;
    private String customerName;
    private int productId;
    private int numberOfItems;
    private Double totalAmount;
    private String paymentMethod;
    private Timestamp transactionDate;

}
