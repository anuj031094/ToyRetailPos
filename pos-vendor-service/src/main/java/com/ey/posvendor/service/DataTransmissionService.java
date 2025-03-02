package com.ey.posvendor.service;

import com.ey.posvendor.dto.TransmitDataDto;
import com.ey.posvendor.model.TransactionData;

import java.util.List;

public interface DataTransmissionService {
    String transmitData(List<TransmitDataDto> transmitDataDto);
}
