package com.ey.posvendor.service;

import com.ey.posvendor.dto.TransmitDataDto;
import com.ey.posvendor.model.TransactionData;
import com.ey.posvendor.model.TransactionDetails;
import com.ey.posvendor.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Service
public class PosServiceImpl implements PosService{

    Logger log = LoggerFactory.getLogger(PosServiceImpl.class);

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private BackOfficeDataTransmissionServiceImpl backOfficeDataTransmissionService;

    @Autowired
    private TransmitDataDto transmitDataDto;

    private final List<TransmitDataDto> transmitDataDtoList = new ArrayList<>();


    public PosServiceImpl(TransactionRepository transactionRepository, BackOfficeDataTransmissionServiceImpl backOfficeDataTransmissionService) {
        this.transactionRepository = transactionRepository;
        this.backOfficeDataTransmissionService = backOfficeDataTransmissionService;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED)
    public void saveTransaction(TransactionData transactionData) {
//        try {
            transactionData.setCreatedAt(new Timestamp(System.currentTimeMillis()));

            log.info("Saving data in Transaction!");
        for (TransactionDetails detail : transactionData.getTransactionDetailsList()) {
            detail.setTransactionData(transactionData); // Set the parent reference
            transmitDataDto.setTransactionId(transactionData.getId());
            transmitDataDto.setProductId(detail.getToyId());
            transmitDataDto.setQuantity(detail.getQuantity());
            transmitDataDto.setPaymentMethod(transactionData.getPaymentMethod());
            transmitDataDto.setCustomerName(transactionData.getCustomerName());
            transmitDataDto.setTotalAmount(transactionData.getTotalAmount());
            transmitDataDtoList.add(transmitDataDto);

        }
            transactionRepository.save(transactionData);

        //Real time data transmit to backend office
        for (TransmitDataDto transmit : transmitDataDtoList) {
            backOfficeDataTransmissionService.transmitData(transmit);
            log.info("Successfully send Data for Transaction ID {} to BackOffice", transmitDataDto.getTransactionId());
        }
        log.info("Complete saving data in Transaction and sent to back office!");

//        }
//        catch (Exception e) {
//            log.error("Error Occured while processing transaction in POS");
//        }
    }
}

//