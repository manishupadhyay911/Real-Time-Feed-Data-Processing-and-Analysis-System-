package com.example.finaldemo.helper.mapper;

import com.example.finaldemo.proto.*;
import com.example.finaldemo.proto.entity.*;

import java.util.ArrayList;
import java.util.List;

public class DTOMapper {

    public static UserDTO.Builder userDTO(UserEntity userEntity) {
        return UserDTO.newBuilder()
                .setName(userEntity.getName())
                .setEmail(userEntity.getEmail())
                .setBalance(userEntity.getBalance());
    }

    public static List<UserDTO> userDTOList(List<UserEntity> userEntityList) {
        List<UserDTO> userDTOList = new ArrayList<>();
        for(UserEntity  userEntity: userEntityList) {
            userDTOList.add(DTOMapper.userDTO(userEntity).build());
        }
        return userDTOList;
    }

    public static AssetDTO assetDTO(AssetEntity assetEntity) {
        return AssetDTO.newBuilder()
                .setMetal(assetEntity.getCode())
                .setCurrency(assetEntity.getCurrency())
                .setDate(assetEntity.getDate())
                .setValue(assetEntity.getValue())
                .setPerformance(assetEntity.getPerformance())
                .setCreatedTs(assetEntity.getCreatedTs())
                .setUpdatedTs(assetEntity.getUpdatedTs())
                .build();
    }
    public static List<AssetDTO> assetDTOList(List<AssetEntity> assetEntityList) {
        List<AssetDTO> assetDTOList = new ArrayList<>();
        for(AssetEntity  assetEntity: assetEntityList) {
            assetDTOList.add(DTOMapper.assetDTO(assetEntity));
        }
        return assetDTOList;
    }

    public static AssetPageResponse assetPageResponse(AssetEntityPageResponse assetEntityPageResponse) {
        AssetPageResponse.Builder assetPageResponse = AssetPageResponse.newBuilder();
        for(AssetEntity assetEntity : assetEntityPageResponse.getItemsList()) {
            assetPageResponse.addItems(DTOMapper.assetDTO(assetEntity));
        }
        assetPageResponse.setToken(assetEntityPageResponse.getToken())
                .setReverseToken(assetEntityPageResponse.getReverseToken())
                .setHasPrev(assetEntityPageResponse.getHasPrev())
                .setHasNext(assetEntityPageResponse.getHasNext());
        return assetPageResponse.build();
    }

    public static UserAssetDTO userAssetDTO(UserAssetEntity userAssetEntity) {
        return UserAssetDTO.newBuilder()
                .setAssetName(userAssetEntity.getAssetName())
                .setQuantity(userAssetEntity.getQuantity())
                .setEmail(userAssetEntity.getEmail())
                .setCreatedTs(userAssetEntity.getCreatedTs())
                .setUpdatedTs(userAssetEntity.getUpdatedTs())
                .build();
    }

    public static UserAssetDTOList userAssetDTOList(UserAssetEntityList userAssetEntityList) {
        UserAssetDTOList.Builder userAssetDTOList = UserAssetDTOList.newBuilder();
        for(UserAssetEntity userAssetEntity : userAssetEntityList.getUserAssetList()) {
            userAssetDTOList.addUserAsset(DTOMapper.userAssetDTO(userAssetEntity));
        }
        return userAssetDTOList.build();
    }

    public static UserAssetResponseDTO userAssetResponseDto(UserAssetEntityResponseDto userAssetEntityResponseDto) {
        return UserAssetResponseDTO.newBuilder()
                .setUser(DTOMapper.userDTO(userAssetEntityResponseDto.getUser()))
                .setUserAssetDtoList(DTOMapper.userAssetDTOList(userAssetEntityResponseDto.getUserAssetEntityList()))
                .build();
    }
    private DTOMapper() {
    }

}
