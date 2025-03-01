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

import static com.ey.posvendor.constants.PosConstants.*;


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
        try {

            log.info("Saving data in Transaction Table!");
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

            //Real time data transmit to backend office
            log.info("Number of records to process : {}", transmitDataDtoList.size());
            for (TransmitDataDto transmit : transmitDataDtoList) {
                transmit.setTransactionId(transactionData.getId());
            }
                String responseFromInventoryService = backOfficeDataTransmissionService.transmitData(transmitDataDtoList);

                // Logic to check if we have inventory is updated for this transaction
                if (STOCK_INVENTORY_UPDATED.equals(responseFromInventoryService)) {

                    log.info("Data saved successfully in Transaction Table!");

                    transactionData.setCreatedAt(new Timestamp(System.currentTimeMillis()));
                    transactionRepository.save(transactionData);
                    log.info("Successfully send Data for Transaction ID {} to BackOffice", transmitDataDtoList.get(0).getTransactionId());
                }

                // check if there is no product found for this product
                else if (NO_INVENTORY_FOUND.equals(responseFromInventoryService)) {
                    log.info("No product found with product id : {}", transmitDataDtoList.get(0).getProductId());
//                    reverseTransactionUpdate(transmit.getProductId(), transmit.getQuantity());
                }

                // check if there is sufficient product to fulfill transaction
                else if (INSUFFICIENT_STOCK.equals(responseFromInventoryService)) {
                    log.info("Insufficient Stock for product id : {}", transmitDataDtoList.get(0).getProductId());
//                    reverseTransactionUpdate(transmit.getProductId(), transmit.getQuantity());
                }

        } catch (Exception e) {
            log.error("Error Occured while processing transaction in POS");
        }
    }
}
