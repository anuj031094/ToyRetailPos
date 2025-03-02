package com.ey.posvendor.constants;

public class PosConstants {
    public static final String STOCK_INVENTORY_UPDATED = "StockInventoryUpdated";
    public static final String NO_INVENTORY_FOUND = "InventoryNotFound";
    public static final String INSUFFICIENT_STOCK = "InsufficientStock";
    public static final String TRANSACTION_ID = "transactionId";
    public static final String ERROR_TRANSACTION_POS_ERROR_MESSAGE = "Error Occured while processing transaction in POS";
    public static final String INSUFFICIENT_STOCK_ERROR_MESSAGE = "Insufficient Stock for this product";
    public static final String NO_PRODUCT_ERROR_MESSAGE = "No product found in inventory!!";
    public static final String TRANSACTION_SUCCESS_MESSAGE = "Transaction is Successfully completed!!";
    public static final String DATA_SENT_SQS_SUCCESS ="Data sent successfully to sqs";
    public static final String DATA_SENT_SQS_ERROR =    "Error occured while sending data.";
    public static final String INVENTORY_SERVICE_URL = "http://localhost:8081/inventory/update";

}
