package com.example.finaldemo.service.impl;

import com.example.finaldemo.constants.ResourceConstants;
import com.example.finaldemo.dao.UserDAO;
import com.example.finaldemo.proto.*;
import com.example.finaldemo.proto.entity.UserAssetEntityList;
import com.example.finaldemo.proto.entity.UserAssetEntityResponseDto;
import com.example.finaldemo.proto.entity.UserEntity;
import com.example.finaldemo.service.UserService;
import com.example.finaldemo.utility.exception.SQLExceptionHandler;
import com.example.finaldemo.utility.exception.UserExceptionHandler;
import com.example.finaldemo.helper.mapper.DTOMapper;
import com.example.finaldemo.helper.mapper.EntityMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;


@Service(ResourceConstants.SERVICE_USER)
public class UserServiceImpl implements UserService {
    private final UserDAO userDAO;

    public UserServiceImpl(@Qualifier(ResourceConstants.DAO_USER) UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public UserResponseDto addUser(UserDTO userDto) {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(10, new SecureRandom());
        String encodedPassword = bCryptPasswordEncoder.encode(userDto.getPassword());

        UserEntity.Builder userEntity = EntityMapper.userEntity(userDto);
        boolean created;
        try {
            created = userDAO.addUser(userEntity, encodedPassword);
        } catch (Exception e) {
           throw new SQLExceptionHandler(e.getCause().getMessage());
        }
        if (!created) {
            throw new UserExceptionHandler("User Registration Unsuccessful");
        }
        UserResponseDto.Builder userResponseDto = UserResponseDto.newBuilder()
                .addData(userDto)
                .setStatus("User Registered Successfully");
        return userResponseDto.build();
    }

    @Override
    public UserResponseDto allUser() {
        return userDAO.allUser();
    }

    @Override
    public UserAssetResponseDTO fetchUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new SQLExceptionHandler("login required");
        }
        String email = authentication.getName();
        UserAssetEntityResponseDto userAssetEntityResponseDto = userDAO.fetchUserInfo(email);
        if (userAssetEntityResponseDto == null) {
            throw new UserExceptionHandler("unauthorized access");
        }
        return DTOMapper.userAssetResponseDto(userAssetEntityResponseDto);
    }

    @Override
    public double getTotalBalance(String email) {
        return userDAO.getTotalBalance(email);
    }

    @Override
    public void setTotalBalanceUser(String email, double balance) {

        userDAO.setTotalBalanceUser(email, balance);
    }

    @Override
    public void setAssetUser(String name, int unit, String metal, boolean buy) {
        userDAO.setAssetUser(name, unit, metal, buy);
    }

    @Override
    public UserAssetDTOList getAssetUser(String email) {
        UserAssetEntityList userAssetEntityList = userDAO.getAssetUser(email);
        return DTOMapper.userAssetDTOList(userAssetEntityList);
    }
}
