package com.ey.backoffice.dto;


import org.springframework.stereotype.Component;

import java.sql.Timestamp;

@Component
public class TransmitDataDto {

    private Integer transactionId;
    private Integer productId;
    private Integer quantity;
    private String customerName;
    private Double totalAmount;
    private String paymentMethod;
    private Timestamp transactionDate;

    public Integer getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Integer transactionId) {
        this.transactionId = transactionId;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Timestamp getCreatedAt() {
        return transactionDate;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.transactionDate = createdAt;
    }
}

