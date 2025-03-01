package com.ey.posvendor.service;

import com.ey.posvendor.dto.TransmitDataDto;
import com.ey.posvendor.model.TransactionData;
import com.ey.posvendor.model.TransactionDetails;
import com.ey.posvendor.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PosServiceImpl implements PosService{

    Logger log = LoggerFactory.getLogger(PosServiceImpl.class);

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private BackOfficeDataTransmissionServiceImpl backOfficeDataTransmissionService;



    public PosServiceImpl(TransactionRepository transactionRepository, BackOfficeDataTransmissionServiceImpl backOfficeDataTransmissionService) {
        this.transactionRepository = transactionRepository;
        this.backOfficeDataTransmissionService = backOfficeDataTransmissionService;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED)
    public void saveTransaction(TransactionData transactionData) {
//        try {

            log.info("Saving data in Transaction!");
            List<TransmitDataDto> transmitDataDtoList = new ArrayList<>();
            for (TransactionDetails detail : transactionData.getTransactionDetailsList()) {
                TransmitDataDto transmitDataDto = new TransmitDataDto();
                transmitDataDto.setProductId(detail.getToyId());
                transmitDataDto.setQuantity(detail.getQuantity());
                transmitDataDto.setPaymentMethod(transactionData.getPaymentMethod());
                transmitDataDto.setCustomerName(transactionData.getCustomerName());
                transmitDataDto.setTotalAmount(transactionData.getTotalAmount());
                transmitDataDtoList.add(transmitDataDto);

                detail.setTransactionData(transactionData); // Set the parent reference
            }

        transactionData.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        transactionRepository.save(transactionData);

        //Real time data transmit to backend office
        log.info("Number of records to process : {}", transmitDataDtoList.size());
        for (TransmitDataDto transmit : transmitDataDtoList) {
            transmit.setTransactionId(transactionData.getId());
            backOfficeDataTransmissionService.transmitData(transmit);
            log.info("Successfully send Data for Transaction ID {} to BackOffice",transactionData.getId());
        }
        log.info("Complete saving data in Transaction and sent to back office!");

//        }
//        catch (Exception e) {
//            log.error("Error Occured while processing transaction in POS");
//        }
    }

    //Compensation handler : Incase of any error reverse all transaction from transactionData
    @KafkaListener(topics = "UndoFailedTransaction",
            groupId = "transaction-group-id",
            clientIdPrefix = "json",
            containerFactory ="transactionListener")
    public void reverseTransactionUpdate(TransmitDataDto transmitDataDto){
        log.info("DELETING TRANSACTION FOR TRANSACTION ID: {}", transmitDataDto.getTransactionId());
        TransactionData deleteTransactionData = transactionRepository.findById(transmitDataDto.getTransactionId())
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
        transactionRepository.delete(deleteTransactionData);
        log.info("DELETED TRANSACTION FOR TRANSACTION ID: {}", transmitDataDto.getTransactionId());
    }
}

//