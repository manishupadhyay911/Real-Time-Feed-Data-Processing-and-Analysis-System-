package com.example.finaldemo.controller;

import com.example.finaldemo.proto.entity.TransactionEntity;
import com.example.finaldemo.proto.entity.TransactionResponse;
import com.example.finaldemo.proto.UserDTO;
import com.example.finaldemo.proto.UserResponseDto;
import com.example.finaldemo.service.TransactionService;
import com.example.finaldemo.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {
    @Mock
    UserService userService;
    @Mock
    TransactionService transactionService;
    @InjectMocks
    UserController userController;
    @InjectMocks
    TransactionController transactionController;

    @Test
    void register() {
        UserDTO.Builder userDto = UserDTO.newBuilder();
        userDto.setName("TestUser");
        userDto.setEmail("testUser@gmail.com");
        userDto.setPassword("12345");

        UserResponseDto.Builder userResponseDto=UserResponseDto.newBuilder();
        userResponseDto.addData(userDto.build());
        userResponseDto.setStatus("Successfully created");

        UserResponseDto expected = userResponseDto.build();

        Mockito.when(userService.addUser(userDto.build())).thenReturn(expected);

        ResponseEntity<UserResponseDto> actual = userController.register(userDto.build());

        assertAll(
                ()->assertNotNull(actual.getBody()),
                ()->assertEquals(HttpStatus.CREATED, actual.getStatusCode()),
                ()->assertEquals(expected, actual.getBody())
        );

    }

    @Test
    void allUser() {
        UserResponseDto.Builder userResponseDto=UserResponseDto.newBuilder();
        userResponseDto.setStatus("Successfully created");

        UserResponseDto expected = userResponseDto.build();

        Mockito.when(userService.allUser()).thenReturn(expected);

        ResponseEntity<UserResponseDto> actual = userController.allUser();

        assertAll(
                ()->assertNotNull(actual.getBody()),
                ()->assertEquals(HttpStatus.OK, actual.getStatusCode()),
                ()->assertEquals(expected, actual.getBody())
        );
    }

    @Test
    void buy() {
        String metal="";
        int quantity=0;
        TransactionResponse.Builder transactionResponse = TransactionResponse.newBuilder();
        transactionResponse.setTransactionId("12345");
        transactionResponse.setStatus("Pending");
        TransactionResponse expected = transactionResponse.build();

        Mockito.when(transactionService.buy(metal,quantity)).thenReturn(expected);

        ResponseEntity<TransactionResponse> actual = transactionController.buy(metal,quantity);

        assertAll(
                ()->assertNotNull(actual.getBody()),
                ()->assertEquals(HttpStatus.OK, actual.getStatusCode()),
                ()->assertEquals(expected, actual.getBody())
        );
    }

    @Test
    void sell() {
        String metal="";
        int quantity=0;
        TransactionResponse.Builder transactionResponse = TransactionResponse.newBuilder();
        transactionResponse.setTransactionId("12345");
        transactionResponse.setStatus("Pending");
        TransactionResponse expected = transactionResponse.build();

        Mockito.when(transactionService.sell(metal,quantity)).thenReturn(expected);

        ResponseEntity<TransactionResponse> actual = transactionController.sell(metal,quantity);

        assertAll(
                ()->assertNotNull(actual.getBody()),
                ()->assertEquals(HttpStatus.OK, actual.getStatusCode()),
                ()->assertEquals(expected, actual.getBody())
        );
    }

}