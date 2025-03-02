package com.ey.posvendor.service;

import com.ey.posvendor.dto.TransmitDataDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class BackOfficeDataTransmissionServiceImpl implements DataTransmissionService{

    Logger log = LoggerFactory.getLogger(BackOfficeDataTransmissionServiceImpl.class);

    @Autowired
    private RestTemplate restTemplate;

    private static final String INVENTORY_SERVICE_URL = "http://localhost:8081";  // Change this to the actual Inventory Service URL

    @Override
    public String transmitData(List<TransmitDataDto> transmitDataDtoList) {
            String response = null;

            // Call the Inventory Service to update inventory
            String url = INVENTORY_SERVICE_URL + "/inventory/update";
            response = restTemplate.postForObject(url, transmitDataDtoList, String.class);

            return response;

    }

    //Compensation method to call inventory to rollback
    public String rollBackInventory(List<TransmitDataDto> transmitDataDtoList) {
        String response = null;

        // Call the Inventory Service to update inventory
        String url = INVENTORY_SERVICE_URL + "/inventory/rollback";
        response = restTemplate.postForObject(url, transmitDataDtoList, String.class);

        return response;

    }
}
