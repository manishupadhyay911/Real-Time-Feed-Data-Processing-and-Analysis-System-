package com.example.finaldemo.dao.impl;

import com.example.finaldemo.constants.DBConstants;
import com.example.finaldemo.constants.ResourceConstants;
import com.example.finaldemo.dao.AssetDAO;
import com.example.finaldemo.dao.SqlHandler;
import com.example.finaldemo.proto.entity.AssetCode;
import com.example.finaldemo.proto.entity.AssetEntity;
import com.example.finaldemo.proto.entity.AssetEntityList;
import com.example.finaldemo.proto.entity.AssetEntityPageResponse;
import com.example.finaldemo.utility.DateTimeUtils;
import com.example.finaldemo.utility.PaginationUtil;
import com.example.finaldemo.utility.exception.SQLExceptionHandler;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;

@Repository(ResourceConstants.DAO_ASSET)
public class AssetDAOImpl implements AssetDAO {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public AssetDAOImpl(@Qualifier(ResourceConstants.BEAN_JDBC_TEMPLATE) NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    private final RowMapper<AssetEntity> mapper = (rs, rowNum) -> {
        try {
            Timestamp ts = rs.getTimestamp(DBConstants.ASSET_COL_DATE);
            Instant date = ts.toInstant();

            AssetEntity.Builder assetDto = AssetEntity.newBuilder();
            assetDto.setCurrency(rs.getString(DBConstants.ASSET_COL_CURRENCY));
            assetDto.setDate(DateTimeUtils.instantToProtoTimestamp(date));
            assetDto.setCode(rs.getString(DBConstants.ASSET_COL_ASSET_CODE));
            assetDto.setPerformance(rs.getDouble(DBConstants.ASSET_COL_PERFORMANCE));
            assetDto.setValue(rs.getDouble(DBConstants.ASSET_COL_VALUE));
            assetDto.setWeightUnit(rs.getString(DBConstants.ASSET_COL_WEIGHT_UNIT));
            return assetDto.build();
        } catch (SQLException e) {
            throw new SQLExceptionHandler(e.getMessage());
        }
    };

    @Override
    public AssetEntity fetchLiveAssetData(String metal, String currency, String unit) {
        MapSqlParameterSource param = new MapSqlParameterSource()
                .addValue(DBConstants.ASSET_COL_ASSET_CODE, metal)
                .addValue(DBConstants.ASSET_COL_CURRENCY, currency);
        List<AssetEntity> allData = namedParameterJdbcTemplate.query(SqlHandler.LIVE_ASSET_DATA, param, mapper);
        if (!allData.isEmpty()) {
            return allData.get(0);
        } else {
            return null;
        }
    }

    @Override
    public AssetEntityList fetchAssetData() {
        List<AssetEntity> allData = namedParameterJdbcTemplate.query(SqlHandler.FETCH_ALL_ASSET, mapper);
        return AssetEntityList.newBuilder().addAllAssetEntityList(allData).build();
    }

    @Override
    public String fetchAssetName(String metalCode) {

        MapSqlParameterSource param = new MapSqlParameterSource()
                .addValue(DBConstants.ASSET_CODE_COL_ASSET_CODE, metalCode);
        List<AssetCode> assetCode = namedParameterJdbcTemplate.query(SqlHandler.FETCH_ASSET_NAME, param, new RowMapper<AssetCode>() {
            @Override
            public AssetCode mapRow(ResultSet rs, int rowNum) throws SQLException {
                return AssetCode.newBuilder()
                        .setName(rs.getString(DBConstants.ASSET_CODE_COL_ASSET_NAME))
                        .build();
            }
        });
        return assetCode.get(0).getName();
    }

    @Override
    public boolean addAssetData(AssetEntity assetEntity) {

        Timestamp dateTs = DateTimeUtils.protoTimestampToSqlTimestamp(assetEntity.getDate());

        MapSqlParameterSource param = new MapSqlParameterSource()
                .addValue(DBConstants.ASSET_COL_DATE,dateTs)
                .addValue(DBConstants.ASSET_COL_CURRENCY,assetEntity.getCurrency())
                .addValue(DBConstants.ASSET_CODE_COL_ASSET_CODE,assetEntity.getCode())
                .addValue(DBConstants.ASSET_COL_PERFORMANCE,assetEntity.getPerformance())
                .addValue(DBConstants.ASSET_COL_VALUE,assetEntity.getValue())
                .addValue(DBConstants.ASSET_COL_WEIGHT_UNIT,assetEntity.getWeightUnit());
        int rowsAffected = namedParameterJdbcTemplate.update(SqlHandler.INSERT_ASSET_DATA, param);
        return (rowsAffected>0);
    }

    @Override
    public AssetEntityList fetchAssets(Integer offset, Integer limit) {

        MapSqlParameterSource param = new MapSqlParameterSource()
                .addValue(DBConstants.LIMIT, limit)
                .addValue(DBConstants.OFFSET, offset);
        List<AssetEntity> allData = namedParameterJdbcTemplate.query(SqlHandler.FETCH_ALL_ASSET_OFFSET_PAGINATION, param, mapper);
        return AssetEntityList.newBuilder().addAllAssetEntityList(allData).build();
    }

    @Override
    public AssetEntityPageResponse fetchAssets(String pageToken, int limit, boolean reverse, Timestamp to, Timestamp from) {

        String nextToken = "";
        StringBuilder decoded = new StringBuilder();
        MapSqlParameterSource param = new MapSqlParameterSource();
        param
                .addValue("to", to)
                .addValue("from", from);

        if (pageToken.isEmpty()) {
            param
                    .addValue(DBConstants.ASSET_COL_DATE, from)
                    .addValue(DBConstants.ASSET_COL_ASSET_CODE, "")
                    .addValue(DBConstants.ASSET_COL_CURRENCY, "")
                    .addValue(DBConstants.LIMIT, Math.min(100, limit));
        } else {
            decoded.append(PaginationUtil.decode(pageToken));
            String [] values = decoded.toString().split("/");

            if (values.length != 3) {
                return null;
            }
            param.addValue(DBConstants.ASSET_COL_DATE, values[0])
                    .addValue(DBConstants.ASSET_COL_ASSET_CODE, values[1])
                    .addValue(DBConstants.ASSET_COL_CURRENCY, values[2])
                    .addValue(DBConstants.LIMIT, Math.min(100, limit));
        }
        String sql = (reverse) ? SqlHandler.FETCH_ALL_ASSET_PAGE_REVERSE : SqlHandler.FETCH_ALL_ASSET_PAGE_FORWARD;
        List<AssetEntity> allData = namedParameterJdbcTemplate.query(sql, param, mapper);
        if (!allData.isEmpty()) {
            nextToken = allData.get(allData.size()-1).getDate().toString()+'/'+
                    allData.get(allData.size()-1).getCode()+'/'+
                    allData.get(allData.size()-1).getCurrency();
        }
        nextToken = PaginationUtil.encode(nextToken);
        AssetEntityPageResponse.Builder assetEntityPageResponse = AssetEntityPageResponse.newBuilder();
        if (!(reverse && allData.size() < limit)) {
            assetEntityPageResponse.setHasPrev(true);
        }
        if(pageToken.isEmpty()) {
            assetEntityPageResponse.setHasPrev(false);
        }
        if (reverse) {
           String temp = pageToken;
            pageToken = nextToken;
            nextToken = temp;
        }
        assetEntityPageResponse.setReverseToken(pageToken);
        if (nextToken.isEmpty()) {
            assetEntityPageResponse.setToken("");
        } else {
            assetEntityPageResponse.setToken(nextToken);
        }
        if (reverse) {
            Collections.reverse(allData);
        }
        assetEntityPageResponse.addAllItems(allData);
        assetEntityPageResponse.setHasNext(reverse || allData.size() >= limit);
        return assetEntityPageResponse.build();
    }

}
