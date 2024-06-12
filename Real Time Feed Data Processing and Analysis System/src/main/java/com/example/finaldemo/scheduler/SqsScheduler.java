package com.example.finaldemo.scheduler;

import com.example.finaldemo.constants.ResourceConstants;
import com.example.finaldemo.service.TransactionService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SqsScheduler {
    private final TransactionService transactionService;

    public SqsScheduler(@Qualifier(ResourceConstants.SERVICE_TRANSACTION) TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @Scheduled(fixedRate = (5000))
    public void pollAwsSqs() {
            transactionService.processTransaction();
    }
}
