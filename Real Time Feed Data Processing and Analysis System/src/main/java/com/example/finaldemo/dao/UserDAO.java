package com.example.finaldemo.dao;

import com.example.finaldemo.proto.*;
import com.example.finaldemo.proto.entity.UserAssetEntityList;
import com.example.finaldemo.proto.entity.UserAssetEntityResponseDto;
import com.example.finaldemo.proto.entity.UserEntity;

public interface UserDAO {

    public boolean addUser(UserEntity.Builder userEntity, String encodedPassword);

    public UserResponseDto allUser();

    public double getTotalBalance(String email);

    public void setTotalBalanceUser(String email, double balance);

    public void setAssetUser(String name, int unit, String metal, boolean buy);

    public UserAssetEntityList getAssetUser(String email);

    public UserAssetEntityResponseDto fetchUserInfo(String email);

}
