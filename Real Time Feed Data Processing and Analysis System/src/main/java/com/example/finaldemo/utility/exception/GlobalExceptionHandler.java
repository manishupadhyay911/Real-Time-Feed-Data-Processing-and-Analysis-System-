package com.example.finaldemo.utility.exception;

import com.example.finaldemo.proto.ApiResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(SQLExceptionHandler.class)
    public ResponseEntity<ApiResponseDTO> sqlExceptionHandler(SQLExceptionHandler ex) {
        ApiResponseDTO.Builder apiResponseDTO = ApiResponseDTO.newBuilder();
        apiResponseDTO.setStatus(HttpStatus.NOT_FOUND.toString());
        apiResponseDTO.setMessage(ex.getMessage());
        apiResponseDTO.setTimestamp(LocalDateTime.now().toString());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponseDTO.build());
    }

    @ExceptionHandler(ProtoExceptionHandler.class)
    public ResponseEntity<ApiResponseDTO> protoExceptionHandler(ProtoExceptionHandler ex) {
        ApiResponseDTO.Builder apiResponseDTO = ApiResponseDTO.newBuilder();
        apiResponseDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.toString());
        apiResponseDTO.setMessage(ex.getMessage());
        apiResponseDTO.setTimestamp(LocalDateTime.now().toString());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponseDTO.build());
    }

    @ExceptionHandler(SQSConnectionError.class)
    public  ResponseEntity<ApiResponseDTO> sqsConnectionError(SQSConnectionError ex) {
        ApiResponseDTO.Builder apiResponseDTO = ApiResponseDTO.newBuilder();
        apiResponseDTO.setStatus(HttpStatus.UNAUTHORIZED.toString());
        apiResponseDTO.setMessage(ex.getMessage());
        apiResponseDTO.setTimestamp(LocalDateTime.now().toString());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiResponseDTO.build());
    }

    @ExceptionHandler(AssetExceptionHandler.class)
    public ResponseEntity<ApiResponseDTO> assetExceptionHandler(AssetExceptionHandler ex){
        ApiResponseDTO.Builder apiResponseDTO = ApiResponseDTO.newBuilder();
        apiResponseDTO.setStatus(HttpStatus.NOT_FOUND.toString());
        apiResponseDTO.setMessage(ex.getMessage());
        apiResponseDTO.setTimestamp(LocalDateTime.now().toString());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponseDTO.build());
    }

    @ExceptionHandler(TransactionFailureException.class)
    public ResponseEntity<ApiResponseDTO> transactionExceptionFailure(TransactionFailureException ex){
        ApiResponseDTO.Builder apiResponseDTO = ApiResponseDTO.newBuilder();
        apiResponseDTO.setStatus(HttpStatus.BAD_REQUEST.toString());
        apiResponseDTO.setMessage(ex.getMessage());
        apiResponseDTO.setTimestamp(LocalDateTime.now().toString());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponseDTO.build());
    }

    @ExceptionHandler(UserExceptionHandler.class)
    public ResponseEntity<ApiResponseDTO> userExceptionHandler(UserExceptionHandler ex){
        ApiResponseDTO.Builder apiResponseDTO = ApiResponseDTO.newBuilder();
        apiResponseDTO.setStatus(HttpStatus.BAD_REQUEST.toString());
        apiResponseDTO.setMessage(ex.getMessage());
        apiResponseDTO.setTimestamp(LocalDateTime.now().toString());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponseDTO.build());
    }

}
