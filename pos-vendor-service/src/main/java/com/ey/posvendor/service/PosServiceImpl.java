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
import java.util.Optional;

import static com.ey.posvendor.constants.PosConstants.*;


@Service
public class PosServiceImpl implements PosService{

    Logger LOGGER = LoggerFactory.getLogger(PosServiceImpl.class);

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private BackOfficeDataTransmissionServiceImpl backOfficeDataTransmissionService;

    List<TransmitDataDto> transmitDataDtoList = new ArrayList<>();

    public PosServiceImpl(TransactionRepository transactionRepository, BackOfficeDataTransmissionServiceImpl backOfficeDataTransmissionService) {
        this.transactionRepository = transactionRepository;
        this.backOfficeDataTransmissionService = backOfficeDataTransmissionService;
    }

    private List<TransmitDataDto> populateTransmitDataDto(TransactionData transactionData) {
        List<TransmitDataDto> transmitDataDtoList = new ArrayList<>();
        for (TransactionDetails detail : transactionData.getTransactionDetailsList()) {
            TransmitDataDto transmitDataDto = new TransmitDataDto();
            transmitDataDto.setProductId(detail.getProductId());
            transmitDataDto.setQuantity(detail.getQuantity());
            transmitDataDto.setPaymentMethod(transactionData.getPaymentMethod());
            transmitDataDto.setCustomerName(transactionData.getCustomerName());
            transmitDataDto.setTotalAmount(transactionData.getTotalAmount());
            transmitDataDto.setTransactionId(transactionData.getId());
            transmitDataDto.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            transmitDataDtoList.add(transmitDataDto);
            detail.setTransactionData(transactionData); // Set the parent reference
        }
        return transmitDataDtoList;
    }

    @Transactional
    public String saveTransaction(TransactionData transactionData) {

        LOGGER.info("Saving data in Transaction Table!");

        List<TransmitDataDto> transmitDataDtoList = populateTransmitDataDto(transactionData);
        String responseFromInventoryService = null;
        try {

            //Real time data transmit to backend office
            LOGGER.info("Checking inventory in back office for  {} product", transmitDataDtoList.size());

                transactionData.setCreatedAt(new Timestamp(System.currentTimeMillis()));
                transactionRepository.save(transactionData);

            for (TransmitDataDto transmitDataDto : transmitDataDtoList) {
                transmitDataDto.setTransactionId(transactionData.getId());
            }

                responseFromInventoryService = backOfficeDataTransmissionService.transmitData(transmitDataDtoList);

                // Logic to check if we have inventory is updated for this transaction
                if (STOCK_INVENTORY_UPDATED.equals(responseFromInventoryService)) {
                    LOGGER.info("Data saved successfully for Transaction ID {}", transactionData.getId());
                    return TRANSACTION_SUCCESS_MESSAGE;
                }

                // check if there is no product found for this product
                else if (NO_INVENTORY_FOUND.equals(responseFromInventoryService)) {
                    LOGGER.info("No product found with product id : {}", transmitDataDtoList.get(0).getProductId());
                    reverseTransactionUpdate(transactionData);
                    return NO_PRODUCT_ERROR_MESSAGE;
                }

                // check if there is sufficient product to fulfill transaction
                else if (INSUFFICIENT_STOCK.equals(responseFromInventoryService)) {
                    LOGGER.info("Insufficient Stock for product id : {}", transmitDataDtoList.get(0).getProductId());
                    reverseTransactionUpdate(transactionData);
                    return INSUFFICIENT_STOCK_ERROR_MESSAGE;
                }
            return "Transaction Unsuccessfull!!";
        } catch (Exception e) {
            LOGGER.error("Error Occured while processing transaction in POS");
            reverseTransactionUpdate(transactionData);
            return ERROR_TRANSACTION_POS_ERROR_MESSAGE;
        }
    }

    public void reverseTransactionUpdate( TransactionData transactionData) {
        LOGGER.info("ROLLBACKING TRANSACTION RECORD FOR TRANSACTION ID: {}", transactionData.getId());
        Optional<TransactionData> deleteTransactionData = transactionRepository.findById(transactionData.getId());
        deleteTransactionData.ifPresent(data -> transactionRepository.delete(data));
        LOGGER.info("ROLLBACKED TRANSACTION RECORD FOR TRANSACTION ID: {}", transactionData.getId());
    }
}
