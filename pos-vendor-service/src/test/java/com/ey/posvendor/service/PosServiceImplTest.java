package com.ey.posvendor.service;

import com.ey.posvendor.dto.TransmitDataDto;
import com.ey.posvendor.model.TransactionData;
import com.ey.posvendor.model.TransactionDetails;
import com.ey.posvendor.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PosServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private BackOfficeDataTransmissionServiceImpl backOfficeDataTransmissionService;

    @InjectMocks
    private PosServiceImpl posService;

    private TransactionData transactionData;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Create a sample TransactionData object with a list of TransactionDetails
        transactionData = new TransactionData();
        transactionData.setId(1);
        transactionData.setPaymentMethod("Credit Card");
        transactionData.setCustomerName("John Doe");
        transactionData.setTotalAmount(200.00);
        transactionData.setCreatedAt(new Timestamp(System.currentTimeMillis()));

        List<TransactionDetails> detailsList = new ArrayList<>();
        TransactionDetails detail = new TransactionDetails();
        detail.setProductId(101);
        detail.setQuantity(2);
        detailsList.add(detail);
        transactionData.setTransactionDetailsList(detailsList);
    }

    @Test
    void testSaveTransaction_Success() {
        // Arrange
        List<TransmitDataDto> transmitDataDtoList = new ArrayList<>();
        TransmitDataDto dto = new TransmitDataDto();
        dto.setTransactionId(1);
        dto.setProductId(101);
        dto.setQuantity(2);
        dto.setPaymentMethod("Credit Card");
        dto.setCustomerName("John Doe");
        dto.setTotalAmount(200.00);
        dto.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        transmitDataDtoList.add(dto);

        // Mocking the behavior of the backOfficeDataTransmissionService
        when(backOfficeDataTransmissionService.transmitData(any(List.class))).thenReturn("StockInventoryUpdated");

        // Mocking the behavior of the transactionRepository to save the transaction data
        when(transactionRepository.save(any(TransactionData.class))).thenReturn(transactionData);

        // Act
        String result = posService.saveTransaction(transactionData);

        // Assert
        assertEquals("Transaction is Successfully completed!!", result);  // Replace with actual success message
        verify(transactionRepository, times(1)).save(transactionData);
    }

    @Test
    void testSaveTransaction_NoInventoryFound() {
        // Arrange
        List<TransmitDataDto> transmitDataDtoList = new ArrayList<>();
        TransmitDataDto dto = new TransmitDataDto();
        dto.setTransactionId(1);
        dto.setProductId(101);
        dto.setQuantity(2);
        dto.setPaymentMethod("Credit Card");
        dto.setCustomerName("John Doe");
        dto.setTotalAmount(200.00);
        dto.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        transmitDataDtoList.add(dto);

        // Mocking the behavior of the backOfficeDataTransmissionService
        when(backOfficeDataTransmissionService.transmitData(any(List.class))).thenReturn("InventoryNotFound");

        // Mocking the behavior of the transactionRepository to save the transaction data
        when(transactionRepository.save(any(TransactionData.class))).thenReturn(transactionData);

        String result = posService.saveTransaction(transactionData);

        // Assert
        assertEquals("No product found in inventory!!", result);
        verify(transactionRepository, times(1)).save(transactionData);
    }

    @Test
    void testSaveTransaction_InsufficientStock() {
        // Arrange
        List<TransmitDataDto> transmitDataDtoList = new ArrayList<>();
        TransmitDataDto dto = new TransmitDataDto();
        dto.setTransactionId(1);
        dto.setProductId(101);
        dto.setQuantity(2);
        dto.setPaymentMethod("Credit Card");
        dto.setCustomerName("John Doe");
        dto.setTotalAmount(200.00);
        dto.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        transmitDataDtoList.add(dto);

        // Mocking the behavior of the backOfficeDataTransmissionService
        when(backOfficeDataTransmissionService.transmitData(any(List.class))).thenReturn("InsufficientStock");

        // Mocking the behavior of the transactionRepository to save the transaction data
        when(transactionRepository.save(any(TransactionData.class))).thenReturn(transactionData);

        // Act
        String result = posService.saveTransaction(transactionData);

        // Assert
        assertEquals("Insufficient Stock for this product", result);  // Replace with actual error message
        verify(transactionRepository, times(1)).save(transactionData); // Ensure rollback happens
    }
}