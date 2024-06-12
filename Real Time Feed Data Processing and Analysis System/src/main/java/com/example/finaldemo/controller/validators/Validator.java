package com.example.finaldemo.controller.validators;

import com.example.finaldemo.proto.UserDTO;
import com.example.finaldemo.utility.exception.UserExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class Validator {
     public static final Logger LOGGER = LoggerFactory.getLogger(Validator.class);
    public static void validateToCreateAccountDTO(UserDTO userDTO) {
        List<String> missingFields = new ArrayList<>();
        if (!StringUtils.hasText(userDTO.getEmail())) {
            missingFields.add("email");
        }
        if (!StringUtils.hasText(userDTO.getName())) {
            missingFields.add("name");
        }
        if (!StringUtils.hasText(userDTO.getPassword())) {
            missingFields.add("password");
        }
        if (!missingFields.isEmpty()) {
            String missing = String.join(", ", missingFields);
            LOGGER.error("Missing fields : {} to create Account", missing);
            throw new UserExceptionHandler(missing);
        }
    }

    private Validator() {
    }
}
