package com.example.finaldemo.dao.impl;

import com.example.finaldemo.constants.DBConstants;
import com.example.finaldemo.constants.ResourceConstants;
import com.example.finaldemo.dao.SqlHandler;
import com.example.finaldemo.dao.UserDAO;
import com.example.finaldemo.proto.*;
import com.example.finaldemo.proto.entity.UserAssetEntity;
import com.example.finaldemo.proto.entity.UserAssetEntityList;
import com.example.finaldemo.proto.entity.UserAssetEntityResponseDto;
import com.example.finaldemo.proto.entity.UserEntity;
import com.example.finaldemo.service.AssetService;
import com.example.finaldemo.utility.exception.SQLExceptionHandler;
import com.example.finaldemo.helper.mapper.DTOMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Repository(ResourceConstants.DAO_USER)
public class UserDAOImpl implements UserDAO {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final AssetService assetService;

    private final RowMapper<UserEntity> userMapper = (rs, rowNum)->{
            try {
                UserEntity.Builder userDto = UserEntity.newBuilder();
                userDto.setName(rs.getString(DBConstants.USER_COL_NAME));
                userDto.setEmail(rs.getString(DBConstants.USER_COL_EMAIL));
                userDto.setBalance(rs.getDouble(DBConstants.USER_COL_BALANCE));
                return userDto.build();

            } catch (SQLException e) {
                throw new SQLExceptionHandler(e.getMessage());
            }
        };

    private final RowMapper<UserAssetEntity> userAssetMapper = (rs, rowNum)->{
        try {
            return UserAssetEntity.newBuilder()
                    .setAssetName(rs.getString(DBConstants.ASSET_CODE_COL_ASSET_NAME))
                    .setQuantity(rs.getInt(DBConstants.USER_ASSET_COL_QUANTITY))
                    .build();

        } catch (SQLException e) {
            throw new SQLExceptionHandler(e.getMessage());
        }
    };

    public UserDAOImpl(@Qualifier(ResourceConstants.BEAN_JDBC_TEMPLATE)
                       NamedParameterJdbcTemplate namedParameterJdbcTemplate,
                       @Qualifier(ResourceConstants.SERVICE_ASSET)
                       AssetService assetService) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.assetService = assetService;
    }

    @Override
    @Transactional
    public boolean addUser(UserEntity.Builder userEntity, String encodedPassword) {
            userEntity
                    .setPassword(encodedPassword)
                    .setBalance(10000);
            int rowsAffected = namedParameterJdbcTemplate.update(SqlHandler.REGISTER_USER, new BeanPropertySqlParameterSource(userEntity.build()));
            if(rowsAffected == 0) {
                return false;
            }
            MapSqlParameterSource param = new MapSqlParameterSource()
                .addValue(DBConstants.USER_COL_EMAIL, userEntity.getEmail());
            rowsAffected = namedParameterJdbcTemplate.update(SqlHandler.REGISTER_USER_ASSET, param);

        return rowsAffected != 0;
    }

    @Override
    public UserResponseDto allUser() {
            List<UserEntity> allData = namedParameterJdbcTemplate.query(SqlHandler.ALL_USER, userMapper);
            List<UserDTO> userDtoList = DTOMapper.userDTOList(allData);
            return UserResponseDto.newBuilder().addAllData(userDtoList).build();
    }

    public double getTotalBalance(String email) {

            MapSqlParameterSource param = new MapSqlParameterSource()
                    .addValue(DBConstants.USER_COL_EMAIL, email);
            List<UserEntity> userEntityList = namedParameterJdbcTemplate.query(SqlHandler.GET_USER_TOTAL_BALANCE, param,
                    (rs, rowNUm)-> UserEntity.newBuilder()
                            .setBalance(rs.getDouble(DBConstants.USER_COL_BALANCE)).build());
            return userEntityList.get(0).getBalance();
    }

    @Override
    public void setTotalBalanceUser(String email, double balance) {

            MapSqlParameterSource param = new MapSqlParameterSource()
                    .addValue(DBConstants.USER_COL_EMAIL, email)
                    .addValue(DBConstants.USER_COL_BALANCE, balance)
                    .addValue(DBConstants.USER_COL_UPDATED_TS,LocalDateTime.now().toString());
            namedParameterJdbcTemplate.update(SqlHandler.SET_TOTAL_USER_BALANCE, param);

    }

    @Override
    public UserAssetEntityList getAssetUser(String email) {

        MapSqlParameterSource param = new MapSqlParameterSource()
                .addValue(DBConstants.USER_ASSET_COL_EMAIL, email);
        List<UserAssetEntity> userAssetEntityList = namedParameterJdbcTemplate.query(SqlHandler.GET_USERASSET, param, userAssetMapper);
        if (userAssetEntityList.isEmpty()) {
            return null;
        }
        return UserAssetEntityList.newBuilder().addAllUserAsset(userAssetEntityList).build();
    }

    @Override
    public void setAssetUser(String email, int unit, String metal, boolean buy) {

            int prevMetalUnit = 0;
        UserAssetEntityList userAssetList = this.getAssetUser(email);
        String metalName = assetService.fetchAssetName(metal);
            for(UserAssetEntity userAsset : userAssetList.getUserAssetList()){
                if (userAsset.getAssetName().compareTo(metalName)==0) {
                    prevMetalUnit = userAsset.getQuantity();
                }
            }
            int newMetalUnit = prevMetalUnit;
            if (buy) {
                newMetalUnit += unit;
            } else {
                newMetalUnit -= unit;
            }

            Timestamp updatedTs = Timestamp.valueOf(LocalDateTime.now());
            MapSqlParameterSource param = new MapSqlParameterSource()
                    .addValue(DBConstants.USER_ASSET_COL_EMAIL, email)
                    .addValue(DBConstants.USER_ASSET_COL_ASSET_CODE, metal)
                    .addValue(DBConstants.USER_ASSET_COL_QUANTITY, newMetalUnit)
                    .addValue(DBConstants.USER_ASSET_COL_UPDATED_TS,updatedTs);
            namedParameterJdbcTemplate.update(SqlHandler.SET_USERASSET_METAL, param);

    }

    @Override
    public UserAssetEntityResponseDto fetchUserInfo(String email) {

        MapSqlParameterSource param = new MapSqlParameterSource()
                .addValue(DBConstants.USER_COL_EMAIL, email);
        List<UserEntity> userEntityList= namedParameterJdbcTemplate.query(SqlHandler.GET_USER, param, userMapper);
        if (userEntityList.isEmpty()) {
            return null;
        }
        UserEntity userEntity = userEntityList.get(0);
        UserAssetEntityList userAssetList = this.getAssetUser(email);
        return UserAssetEntityResponseDto.newBuilder()
                .setUser(userEntity)
                .setUserAssetEntityList(userAssetList)
                .build();

    }
}
