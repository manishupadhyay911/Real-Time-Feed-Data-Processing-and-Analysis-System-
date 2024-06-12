package com.example.finaldemo.utility.exception;

public class SQSConnectionError extends RuntimeException {
    public SQSConnectionError(String message) {
        super(message);
    }
}
