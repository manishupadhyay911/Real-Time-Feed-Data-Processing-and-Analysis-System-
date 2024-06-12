package com.example.finaldemo.controller;

import com.example.finaldemo.constants.URLConstants;
import com.example.finaldemo.constants.ResourceConstants;
import com.example.finaldemo.proto.entity.TransactionEntity;
import com.example.finaldemo.proto.entity.TransactionPageResponse;
import com.example.finaldemo.proto.entity.TransactionResponse;
import com.example.finaldemo.service.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController(ResourceConstants.TRANSACTION_CONTROLLER)
@RequestMapping(URLConstants.PATH_USER)
public class TransactionController {
    private final TransactionService transactionService;
    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionController.class);

    public TransactionController(@Qualifier(ResourceConstants.SERVICE_TRANSACTION) TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping("/buy")
    public ResponseEntity<TransactionResponse> buy(@RequestParam String metal, @RequestParam int quantity) {
        LOGGER.info("Received Request for buying asset");
        TransactionResponse transactionResponse = transactionService.buy(metal, quantity);
        return ResponseEntity.status(HttpStatus.OK).body(transactionResponse);
    }

    @GetMapping("/sell")
    public ResponseEntity<TransactionResponse> sell(@RequestParam String metal, @RequestParam int quantity) {
        LOGGER.info("Received Request for selling asset");
        TransactionResponse transactionResponse = transactionService.sell(metal, quantity);
        return ResponseEntity.status(HttpStatus.OK).body(transactionResponse);
    }

    @GetMapping("/transaction-status")
    public ResponseEntity<TransactionEntity> transactionStatus(@RequestParam String transactionID) {
        LOGGER.info("Received Request to fetch Transaction Status");
        TransactionEntity transactionResponse = transactionService.transactionStatus(transactionID);
        return ResponseEntity.status(HttpStatus.OK).body(transactionResponse);
    }

    @GetMapping("/transaction-history")
    public ResponseEntity<TransactionPageResponse> transactionHistory(@RequestParam String pageToken,
                                                              @RequestParam int limit,
                                                              @RequestParam boolean reverse) {
        LOGGER.info("Received Request to fetch Transaction history paginated");
        TransactionPageResponse transactionList = transactionService.transactionHistory(pageToken, limit, reverse);
        return ResponseEntity.status(HttpStatus.OK).body(transactionList);
    }

}
