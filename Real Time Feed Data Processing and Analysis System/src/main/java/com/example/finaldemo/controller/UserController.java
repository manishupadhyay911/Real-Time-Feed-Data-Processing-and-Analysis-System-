package com.example.finaldemo.controller;

import com.example.finaldemo.constants.ResourceConstants;
import com.example.finaldemo.constants.URLConstants;
import com.example.finaldemo.controller.validators.Validator;
import com.example.finaldemo.proto.*;
import com.example.finaldemo.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController(ResourceConstants.USER_CONTROLLER)
@RequestMapping(URLConstants.PATH_USER)
public class UserController {
    private final UserService userService;
    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    public UserController(@Qualifier(ResourceConstants.SERVICE_USER) UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> register(@RequestBody UserDTO userDTO) {
        LOGGER.info("Received Request for user Registration");
        Validator.validateToCreateAccountDTO(userDTO);
        UserResponseDto userResponseDto = userService.addUser(userDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(userResponseDto);
    }

    @GetMapping("/user-info")
    public ResponseEntity<UserAssetResponseDTO> userInfo() {
        LOGGER.info("Received Request to fetch user information");
        UserAssetResponseDTO userAssetResponseDto = userService.fetchUser();
        return ResponseEntity.status(HttpStatus.OK).body(userAssetResponseDto);
    }

    @GetMapping("/all-users")
    public ResponseEntity<UserResponseDto> allUser() {
        LOGGER.info("Received Request to fetch all user information");
        UserResponseDto userResponseDto = userService.allUser();
        return ResponseEntity.status(HttpStatus.OK).body(userResponseDto);
    }

}
