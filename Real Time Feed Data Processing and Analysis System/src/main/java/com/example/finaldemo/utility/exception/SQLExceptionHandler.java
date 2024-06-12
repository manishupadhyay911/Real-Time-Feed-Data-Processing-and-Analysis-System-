package com.example.finaldemo.utility.exception;

public class SQLExceptionHandler extends RuntimeException {
    public SQLExceptionHandler(String reason) {
        super(reason);
    }
}
