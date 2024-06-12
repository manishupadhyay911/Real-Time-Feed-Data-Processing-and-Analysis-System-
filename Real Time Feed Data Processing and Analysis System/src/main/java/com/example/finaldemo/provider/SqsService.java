package com.example.finaldemo.provider;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.example.finaldemo.constants.ApplicationProperties;
import com.example.finaldemo.proto.entity.TransactionResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SqsService {
    private final AmazonSQS sqsClient;
    private final ApplicationProperties applicationProperties;

    public SqsService(AmazonSQS sqsClient, ApplicationProperties applicationProperties) {
        this.sqsClient = sqsClient;
        this.applicationProperties = applicationProperties;
    }

    public void sendMessage(TransactionResponse transactionResponse)  {
       SendMessageRequest sendMessageRequest = new SendMessageRequest()
               .withQueueUrl(applicationProperties.getAwsSqsName())
               .withMessageBody(transactionResponse.getTransactionId());
       sqsClient.sendMessage(sendMessageRequest);
    }

    public List<Message> pollMessage() {
        return sqsClient.receiveMessage(applicationProperties.getAwsSqsName())
                .getMessages();
    }

    public void deleteMesage(Message m) {
        sqsClient.deleteMessage(applicationProperties.getAwsSqsName(), m.getReceiptHandle());
    }
}
