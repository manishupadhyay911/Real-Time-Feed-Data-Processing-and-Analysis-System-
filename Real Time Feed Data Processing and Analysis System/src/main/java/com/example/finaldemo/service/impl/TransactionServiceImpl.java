package com.example.finaldemo.service.impl;

import com.amazonaws.services.sqs.model.Message;
import com.example.finaldemo.constants.ResourceConstants;
import com.example.finaldemo.dao.TransactionDAO;
import com.example.finaldemo.proto.*;
import com.example.finaldemo.proto.entity.TransactionEntity;
import com.example.finaldemo.proto.entity.TransactionPageResponse;
import com.example.finaldemo.proto.entity.TransactionResponse;
import com.example.finaldemo.proto.enums.Currency;
import com.example.finaldemo.proto.enums.TransactionType;
import com.example.finaldemo.proto.enums.Unit;
import com.example.finaldemo.service.AssetService;
import com.example.finaldemo.service.TransactionService;
import com.example.finaldemo.service.UserService;
import com.example.finaldemo.provider.SqsService;
import com.example.finaldemo.utility.exception.AssetExceptionHandler;
import com.example.finaldemo.utility.exception.ProtoExceptionHandler;
import com.example.finaldemo.utility.exception.TransactionFailureException;
import com.example.finaldemo.utility.exception.UserExceptionHandler;
import com.example.finaldemo.utility.mutex.Mutex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.UUID;

@Service(ResourceConstants.SERVICE_TRANSACTION)
public class TransactionServiceImpl implements TransactionService {
    public static final Logger LOGGER = LoggerFactory.getLogger(TransactionServiceImpl.class);
    private final AssetService assetService;
    private final UserService userService;
    private final SqsService sqsService;
    private final TransactionDAO transactionDAO;
    private final Mutex mutex;

    public TransactionServiceImpl(@Qualifier(ResourceConstants.SERVICE_ASSET) AssetService assetService,
                                  Mutex mutex,
                                  SqsService sqsService,
                                  @Qualifier(ResourceConstants.SERVICE_USER) UserService userService,
                                  @Qualifier(ResourceConstants.DAO_TRANSACTION) TransactionDAO transactionDAO) {
        this.assetService = assetService;
        this.userService = userService;
        this.transactionDAO = transactionDAO;
        this.sqsService = sqsService;
        this.mutex = mutex;

    }

    @Override
    public TransactionResponse buy(String metal, int quantity) {
        AssetDTO assetDTO = assetService.fetchLiveAssetData(metal, ResourceConstants.CURRENCY_SGD, Unit.g.toString());
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new TransactionFailureException("Unauthorized");
        }
        if (quantity <= 0) {
            throw new TransactionFailureException("Invalid quantity");
        }
        String email = authentication.getName();
        TransactionResponse transactionResponse = null;
        String transactionID = UUID.randomUUID().toString();

        try{
            if (mutex.acquireLock(email, transactionID)) {
                double totalBalance = userService.getTotalBalance(email);
                double liveAssetValue = assetDTO.getValue();
                if(totalBalance < liveAssetValue * quantity) {
                    throw new TransactionFailureException("Insufficient Balance");
                }

                transactionResponse = transactionDAO.buy(metal, quantity, liveAssetValue, email, totalBalance, transactionID, TransactionType.BUY.toString());
                sqsService.sendMessage(transactionResponse);
                LOGGER.info("lock acquired");

            }
            else {
                LOGGER.info("waiting to acquire lock");
            }
        }
        catch (Exception e) {
            throw new ProtoExceptionHandler(e.getMessage());
        }
        finally {
            mutex.releaseLock(email, transactionID);
        }
        return transactionResponse;

    }

    @Override
    public TransactionEntity transactionStatus(String transactionID) {
        return transactionDAO.fetchTransactionStatus(transactionID);
    }

    @Override
    public TransactionResponse sell(String metalCode, int quantity) {
        AssetDTO assetDTO = assetService.fetchLiveAssetData(metalCode, Currency.SGD.toString(), Unit.g.toString());
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        String transactionID = UUID.randomUUID().toString();
        TransactionResponse transactionResponse = null;

        try{
            if (mutex.acquireLock(email, transactionID)) {
                double totalBalance = userService.getTotalBalance(email);
                UserAssetDTOList userAssetDTO= userService.getAssetUser(email);
                String metalName = assetService.fetchAssetName(metalCode);
                boolean failed = false;
                for(UserAssetDTO userAsset : userAssetDTO.getUserAssetList()){
                    if (userAsset.getAssetName().compareTo(metalName)==0 && userAsset.getQuantity() < quantity) {
                            failed = true;
                            break;
                    }
                }

                if (failed) {
                    throw new TransactionFailureException("Insufficient units to sell.");
                }
                transactionResponse = transactionDAO.sell(metalCode, quantity, assetDTO.getValue(), email, totalBalance, transactionID, TransactionType.SELL.toString());
                sqsService.sendMessage(transactionResponse);
                LOGGER.info("lock acquired");
            }
            else {
                LOGGER.info("waiting to acquire lock");
            }
        }
        catch (Exception e){
            throw new ProtoExceptionHandler(e.getMessage());
        }
        finally {
            mutex.releaseLock(email, transactionID);
        }
        if (transactionResponse == null) {
            throw new TransactionFailureException("Transaction Failed");
        }
        return transactionResponse;

    }

    @Override
    public TransactionPageResponse transactionHistory(String pageToken, int limit, boolean reverse) {
        if (limit < 0) {
            throw new AssetExceptionHandler("Invalid value provided");
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication==null){
            throw new UserExceptionHandler("User not logged in");
        }
        String email = authentication.getName();
        TransactionPageResponse transactionList = transactionDAO.fetchTransactionHistory(email, pageToken, limit, reverse);
        if (transactionList == null) {
            throw new AssetExceptionHandler("Invalid Token provided");
        }
        return transactionList;
    }

    @Override
    public void processTransaction() {
        List<Message> transactionIdList = sqsService.pollMessage();
        if (transactionIdList.isEmpty()) {
            return;
        }
        Message message = transactionIdList.get(0);
        String transactionID = message.getBody();
        TransactionEntity transactionEntity = this.transactionStatus(transactionID);
        boolean value;

        if (transactionEntity.getTransactionType().compareTo(TransactionType.BUY.toString()) == 0) {
            value = transactionDAO.processBuyTransaction(transactionEntity, message);
            if (!value) {
                throw new TransactionFailureException("transaction id: " + transactionEntity.getTransactionId()+" Failed due to Insufficient Balance");
            }
        } else {
            value = transactionDAO.processSellTransaction(transactionEntity, message);
            if (!value) {
                throw new TransactionFailureException("transaction id: " + transactionEntity.getTransactionId()+" Failed due to Insufficient units");
            }
        }
        sqsService.deleteMesage(message);

    }
}
