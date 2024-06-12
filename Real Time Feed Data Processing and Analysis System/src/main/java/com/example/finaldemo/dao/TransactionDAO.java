package com.example.finaldemo.dao;

import com.amazonaws.services.sqs.model.Message;
import com.example.finaldemo.proto.*;

import com.example.finaldemo.proto.entity.AssetEntity;
import com.example.finaldemo.proto.entity.TransactionEntity;
import com.example.finaldemo.proto.entity.TransactionPageResponse;
import com.example.finaldemo.proto.entity.TransactionResponse;
import com.google.protobuf.InvalidProtocolBufferException;

import java.util.UUID;

public interface TransactionDAO {

    public TransactionResponse buy(String metal, int quantity, double liveAssetValue, String email, double totalBalance, String uuid, String transactionType) throws InvalidProtocolBufferException;

    public TransactionResponse sell(String metal, int quantity, double liveAssetValue, String email, double totalBalance, String uuid, String transactionType) throws InvalidProtocolBufferException;

    public boolean processSellTransaction(TransactionEntity transactionEntity, Message message);

    public boolean processBuyTransaction(TransactionEntity transactionEntity, Message message);

    public void setTransactionStatus(String transactionId, String status);

    public TransactionEntity fetchTransactionStatus(String transactionID);


    public TransactionPageResponse fetchTransactionHistory(String email, String pageToken, int limit, boolean reverse);
}
