package com.ey.posvendor.model;

import jakarta.persistence.*;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

@Entity
@Table(name = "pos_transaction")
public class TransactionData implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Column(name = "customer_name")
    private String customerName;
    @Column(name = "total_amount")
    private Double totalAmount;
    @Column(name ="payment_method")
    private String paymentMethod;
    @Column(name = "sent_to_sales")
    private Boolean sentToSales;
    @Column(name = "created_at")
    private Timestamp createdAt;

    @OneToMany(
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            mappedBy = "transactionData"
    )
    private List<TransactionDetails> transactionDetailsList ;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public Boolean getSentToSales() {
        return sentToSales;
    }

    public void setSentToSales(Boolean sentToSales) {
        this.sentToSales = sentToSales;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Timestamp getCreateAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public List<TransactionDetails> getTransactionDetailsList() {
        return transactionDetailsList;
    }

    public void setTransactionDetailsList(List<TransactionDetails> transactionDetailsList) {
        this.transactionDetailsList = transactionDetailsList;
    }
}
