package com.ey.posvendor.service;

import com.ey.posvendor.dto.TransmitDataDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static com.ey.posvendor.constants.PosConstants.INVENTORY_SERVICE_URL;

@Service
public class BackOfficeDataTransmissionServiceImpl implements DataTransmissionService{

    Logger log = LoggerFactory.getLogger(BackOfficeDataTransmissionServiceImpl.class);

    @Autowired
    private RestTemplate restTemplate;


    @Override
    public String transmitData(List<TransmitDataDto> transmitDataDtoList) {
            String response = null;

            // Call the Inventory Service to update inventory
            String url = INVENTORY_SERVICE_URL;
            response = restTemplate.postForObject(url, transmitDataDtoList, String.class);

            return response;

    }
}
