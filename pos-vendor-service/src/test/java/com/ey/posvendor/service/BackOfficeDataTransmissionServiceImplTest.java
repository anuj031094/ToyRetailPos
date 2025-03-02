package com.ey.posvendor.service;

import com.ey.posvendor.dto.TransmitDataDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;


class BackOfficeDataTransmissionServiceImplTest {
    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private BackOfficeDataTransmissionServiceImpl backOfficeDataTransmissionService;

    private final List<TransmitDataDto> transmitDataDtoList = new ArrayList<>();

    @BeforeEach
    void setUp() {

        // Initialize mocks and create test data
        MockitoAnnotations.openMocks(this);
        // Create TransmitDataDto objects and set fields using setters
        TransmitDataDto dataDto = new TransmitDataDto();
        dataDto.setTransactionId(1);
        dataDto.setProductId(101);
        dataDto.setQuantity(2);
        dataDto.setCustomerName("John Doe");
        dataDto.setTotalAmount(150.75);
        dataDto.setPaymentMethod("Credit Card");

        TransmitDataDto dataDto1 = new TransmitDataDto();
        dataDto1.setTransactionId(2);
        dataDto1.setProductId(102);
        dataDto1.setQuantity(1);
        dataDto1.setCustomerName("Jane Smith");
        dataDto1.setTotalAmount(75.50);
        dataDto1.setPaymentMethod("PayPal");

        transmitDataDtoList.add(dataDto);
        transmitDataDtoList.add(dataDto1);
    }

    @Test
    void testTransmitData() {
        // Arrange
        String expectedResponse = "Success";  // Example of the expected response
        String url = "http://localhost:8081/inventory/update"; // Assume this is the URL you want to mock
        when(restTemplate.postForObject(eq(url), eq(transmitDataDtoList), eq(String.class)))
                .thenReturn(expectedResponse);

        // Act
        String actualResponse = backOfficeDataTransmissionService.transmitData(transmitDataDtoList);

        // Assert
        assertEquals(expectedResponse, actualResponse);

        // Verify that RestTemplate's postForObject was called once with the correct parameters
        verify(restTemplate, times(1)).postForObject(eq(url), eq(transmitDataDtoList), eq(String.class));
    }

    @Test
    void testTransmitData_withNullResponse() {
        // Arrange
        String url = "http://localhost:8081/inventory/update";
        when(restTemplate.postForObject(eq(url), eq(transmitDataDtoList), eq(String.class)))
                .thenReturn(null);

        String actualResponse = backOfficeDataTransmissionService.transmitData(transmitDataDtoList);

        // Assert
        assertNull(actualResponse);

        // Verify that RestTemplate's postForObject was called once
        verify(restTemplate, times(1)).postForObject(eq(url), eq(transmitDataDtoList), eq(String.class));
    }
}