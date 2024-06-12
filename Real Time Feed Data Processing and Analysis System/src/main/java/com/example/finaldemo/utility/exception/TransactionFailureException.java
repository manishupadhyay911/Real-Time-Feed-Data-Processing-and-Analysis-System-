package com.example.finaldemo.utility.exception;

public class TransactionFailureException extends RuntimeException{
    public TransactionFailureException(String message) {
        super(message);
    }
}
