package com.example.finaldemo.configuration;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AwsSqsConfig {
    @Bean
    public AmazonSQS amazonSQS(){
        return AmazonSQSClientBuilder.standard()
                .withRegion(Regions.EU_NORTH_1)
                .build();
    }

}
