package com.example.finaldemo.helper.mapper;

import com.example.finaldemo.proto.AssetDTO;
import com.example.finaldemo.proto.UserDTO;
import com.example.finaldemo.proto.entity.UserEntity;
import com.example.finaldemo.proto.entity.AssetEntity;

public class EntityMapper {

    public static UserEntity.Builder userEntity(UserDTO userDTO) {
        return UserEntity.newBuilder()
                .setName(userDTO.getName())
                .setEmail(userDTO.getEmail())
                .setPassword(userDTO.getPassword())
                .setBalance(userDTO.getBalance())
                .setUpdatedTs(userDTO.getUpdatedTs())
                .setCreatedTs(userDTO.getCreatedTs());

    }
    public static AssetEntity assetEntity(AssetDTO assetDTO) {
        return AssetEntity.newBuilder()
                .setCode(assetDTO.getMetal())
                .setCurrency(assetDTO.getCurrency())
                .setDate(assetDTO.getDate())
                .setWeightUnit(assetDTO.getWeightUnit())
                .setValue(assetDTO.getValue())
                .setPerformance(assetDTO.getPerformance())
                .setCreatedTs(assetDTO.getCreatedTs())
                .setUpdatedTs(assetDTO.getUpdatedTs())
                .build();
    }
    private EntityMapper() {
    }

}
