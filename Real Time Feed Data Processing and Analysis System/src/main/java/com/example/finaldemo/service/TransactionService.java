package com.example.finaldemo.service;

import com.example.finaldemo.proto.entity.TransactionEntity;
import com.example.finaldemo.proto.entity.TransactionList;
import com.example.finaldemo.proto.entity.TransactionPageResponse;
import com.example.finaldemo.proto.entity.TransactionResponse;

public interface TransactionService {
    public TransactionResponse buy(String metal, int quantity);

    public TransactionEntity transactionStatus(String transactionID);

    public TransactionResponse sell(String metal, int quantity);

    public TransactionPageResponse transactionHistory(String pageToken, int limit, boolean reverse);

    public void processTransaction();
}
