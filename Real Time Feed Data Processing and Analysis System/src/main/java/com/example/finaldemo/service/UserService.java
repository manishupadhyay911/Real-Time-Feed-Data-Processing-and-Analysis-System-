package com.example.finaldemo.service;

import com.example.finaldemo.proto.*;

public interface UserService {
    public UserResponseDto addUser(UserDTO userDto);

    public UserResponseDto allUser();

    public UserAssetResponseDTO fetchUser();

    public double getTotalBalance(String email);

    public void setTotalBalanceUser(String email, double balance);

    public void setAssetUser(String name, int unit, String metal, boolean buy);

    public UserAssetDTOList getAssetUser(String email);

}
