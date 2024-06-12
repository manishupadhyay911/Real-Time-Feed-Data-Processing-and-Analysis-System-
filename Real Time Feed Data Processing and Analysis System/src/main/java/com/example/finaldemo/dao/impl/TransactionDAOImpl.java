package com.example.finaldemo.dao.impl;

import com.amazonaws.services.sqs.model.Message;
import com.example.finaldemo.constants.DBConstants;
import com.example.finaldemo.constants.ResourceConstants;
import com.example.finaldemo.dao.SqlHandler;
import com.example.finaldemo.dao.TransactionDAO;
import com.example.finaldemo.proto.*;
import com.example.finaldemo.proto.entity.TransactionEntity;
import com.example.finaldemo.proto.entity.TransactionPageResponse;
import com.example.finaldemo.proto.entity.TransactionResponse;
import com.example.finaldemo.service.AssetService;
import com.example.finaldemo.service.UserService;
import com.example.finaldemo.utility.DateTimeUtils;
import com.example.finaldemo.utility.PaginationUtil;
import com.google.protobuf.InvalidProtocolBufferException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
@Repository(ResourceConstants.DAO_TRANSACTION)
public class TransactionDAOImpl implements TransactionDAO {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final UserService userService;
    private final AssetService assetService;

    public TransactionDAOImpl(@Qualifier(ResourceConstants.BEAN_JDBC_TEMPLATE) NamedParameterJdbcTemplate namedParameterJdbcTemplate,
                              @Qualifier(ResourceConstants.SERVICE_USER) UserService userService,
                              @Qualifier(ResourceConstants.SERVICE_ASSET) AssetService assetService) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.userService = userService;
        this.assetService = assetService;
    }

    private final RowMapper<TransactionEntity> transactionEntityRowMapper = (rs, rowNum) -> {
        Timestamp ts = rs.getTimestamp(DBConstants.TRANSACTIONS_COL_UPDATED_TS);
        Instant date = ts.toInstant();

        TransactionEntity.Builder transactionResponse = TransactionEntity.newBuilder();
        transactionResponse.setStatus(rs.getString(DBConstants.TRANSACTIONS_COL_STATUS));
        transactionResponse.setTransactionId(rs.getString(DBConstants.TRANSACTIONS_COL_TRANSACTION_ID));
        transactionResponse.setTransactionType(rs.getString(DBConstants.TRANSACTIONS_COL_TRANSACTION_TYPE));
        transactionResponse.setEmail(rs.getString(DBConstants.TRANSACTIONS_COL_EMAIL));
        transactionResponse.setAmount(rs.getDouble(DBConstants.TRANSACTIONS_COL_AMOUNT));
        transactionResponse.setCode(rs.getString(DBConstants.TRANSACTIONS_COL_ASSET_CODE));
        transactionResponse.setCurrency(rs.getString(DBConstants.TRANSACTIONS_COL_CURRENCY));
        transactionResponse.setQuantity(rs.getInt(DBConstants.TRANSACTIONS_COL_QUANTITY));
        transactionResponse.setUpdatedTs(DateTimeUtils.instantToProtoTimestamp(date));
        return transactionResponse.build();
    };

    @Override
    @Transactional
    public TransactionResponse buy(String metal, int quantity,
                                   double liveAssetValue,
                                   String email,
                                   double totalBalance,
                                   String transactionID,
                                   String transactionType) throws InvalidProtocolBufferException {

            TransactionEntity.Builder transactionEntity = TransactionEntity.newBuilder()
                    .setTransactionId(transactionID)
                    .setTransactionType(transactionType)
                    .setEmail(email)
                    .setStatus(ResourceConstants.PENDING)
                    .setAmount(liveAssetValue * quantity)
                    .setCode(metal)
                    .setCurrency(ResourceConstants.CURRENCY_SGD)
                    .setQuantity(quantity);

        MapSqlParameterSource param = new MapSqlParameterSource()
                .addValue(DBConstants.TRANSACTIONS_COL_TRANSACTION_ID, transactionID)
                .addValue(DBConstants.TRANSACTIONS_COL_TRANSACTION_TYPE, transactionType)
                .addValue(DBConstants.TRANSACTIONS_COL_EMAIL, email)
                .addValue(DBConstants.TRANSACTIONS_COL_STATUS, ResourceConstants.PENDING)
                .addValue(DBConstants.TRANSACTIONS_COL_AMOUNT, liveAssetValue * quantity)
                .addValue(DBConstants.TRANSACTIONS_COL_ASSET_CODE, metal)
                .addValue(DBConstants.TRANSACTIONS_COL_CURRENCY, ResourceConstants.CURRENCY_SGD)
                .addValue(DBConstants.TRANSACTIONS_COL_QUANTITY, quantity);

            userService.setTotalBalanceUser(email, totalBalance - (liveAssetValue * quantity));
            namedParameterJdbcTemplate.update(SqlHandler.INSERT_TRANSACTION, param);
            TransactionResponse.Builder transactionResponse = TransactionResponse.newBuilder()
                    .setData(transactionEntity)
                    .setStatus(transactionEntity.getStatus())
                    .setTransactionId(transactionEntity.getTransactionId());
            return transactionResponse.build();
    }

    @Override
    @Transactional
    public TransactionResponse sell(String metal, int quantity,
                                    double liveAssetValue,
                                    String email,
                                    double totalBalance,
                                    String transactionID,
                                    String transactionType)  {

        TransactionEntity.Builder transactionEntity = TransactionEntity.newBuilder()
                .setTransactionId(transactionID)
                .setTransactionType(transactionType)
                .setEmail(email)
                .setStatus(ResourceConstants.PENDING)
                .setAmount(liveAssetValue * quantity)
                .setCode(metal)
                .setCurrency(ResourceConstants.CURRENCY_SGD)
                .setQuantity(quantity);

        MapSqlParameterSource param = new MapSqlParameterSource()
                .addValue(DBConstants.TRANSACTIONS_COL_TRANSACTION_ID, transactionID)
                .addValue(DBConstants.TRANSACTIONS_COL_TRANSACTION_TYPE, transactionType)
                .addValue(DBConstants.TRANSACTIONS_COL_EMAIL, email)
                .addValue(DBConstants.TRANSACTIONS_COL_STATUS, ResourceConstants.PENDING)
                .addValue(DBConstants.TRANSACTIONS_COL_AMOUNT, liveAssetValue * quantity)
                .addValue(DBConstants.TRANSACTIONS_COL_ASSET_CODE, metal)
                .addValue(DBConstants.TRANSACTIONS_COL_CURRENCY, ResourceConstants.CURRENCY_SGD)
                .addValue(DBConstants.TRANSACTIONS_COL_QUANTITY, quantity);

        // credit total balance
        userService.setTotalBalanceUser(email, totalBalance + (liveAssetValue * quantity));
        namedParameterJdbcTemplate.update(SqlHandler.INSERT_TRANSACTION, param);
            TransactionResponse.Builder transactionResponse = TransactionResponse.newBuilder()
                    .setData(transactionEntity)
                    .setStatus(transactionEntity.getStatus())
                    .setTransactionId(transactionEntity.getTransactionId());
        return transactionResponse.build();

    }


    @Override
    @Transactional
    public boolean processSellTransaction(TransactionEntity transactionEntity, Message message) {

            String email = transactionEntity.getEmail();
            String metalCode = transactionEntity.getCode();
            double creditedAmount = transactionEntity.getAmount();
            double totalBalance = userService.getTotalBalance(email);
            int unit = transactionEntity.getQuantity();
            UserAssetDTOList userAssetEntity = userService.getAssetUser(email);
            TransactionResponse.Builder transaction = TransactionResponse.newBuilder();
            transaction.setTransactionId(transactionEntity.getTransactionId());
            boolean failed = false;
            String metalName = assetService.fetchAssetName(metalCode);
            for(UserAssetDTO userAsset : userAssetEntity.getUserAssetList()) {
                if(userAsset.getAssetName().compareTo(metalName)==0 && userAsset.getQuantity() < unit) {
                        failed = true;
                }
            }

            if (failed) {
                transaction.setStatus(ResourceConstants.FAILED);
                setTransactionStatus(transactionEntity.getTransactionId(), ResourceConstants.FAILED);
                userService.setTotalBalanceUser(email, totalBalance - creditedAmount);
                return false;
            }

            setTransactionStatus(transactionEntity.getTransactionId(), ResourceConstants.SUCCESS);
            userService.setAssetUser(email, transactionEntity.getQuantity(), transactionEntity.getCode(), false);
            return true;

    }

    @Override
    @Transactional
    public boolean processBuyTransaction(TransactionEntity transactionEntity, Message message) {

            String transactionId = transactionEntity.getTransactionId();
            String email = transactionEntity.getEmail();
            double debitedAmount = transactionEntity.getAmount();
            double totalBalance = userService.getTotalBalance(email);
            double currentAssetAmount = transactionEntity.getQuantity() * assetService.fetchLiveAssetData(transactionEntity.getCode(), "SGD", "g").getValue();
            TransactionResponse.Builder transaction = TransactionResponse.newBuilder();
            transaction.setTransactionId(transactionId);
            if (currentAssetAmount > totalBalance) {
                transaction.setStatus(ResourceConstants.FAILED);
                userService.setTotalBalanceUser(email, totalBalance + debitedAmount);
                setTransactionStatus(transactionId, ResourceConstants.FAILED);
                return false;
            }

            setTransactionStatus(transactionId, ResourceConstants.SUCCESS);
            userService.setAssetUser(email, transactionEntity.getQuantity(), transactionEntity.getCode(), true);
            return true;

    }

    @Override
    public void setTransactionStatus(String transactionID, String status) {

            MapSqlParameterSource param = new MapSqlParameterSource()
                    .addValue(DBConstants.TRANSACTIONS_COL_TRANSACTION_ID, transactionID)
                    .addValue(DBConstants.TRANSACTIONS_COL_STATUS, status)
                    .addValue(DBConstants.TRANSACTIONS_COL_UPDATED_TS, Timestamp.from(Instant.now()));
            namedParameterJdbcTemplate.update(SqlHandler.UPDATE_TRANSACTION_STATUS, param);
    }

    @Override
    public TransactionEntity fetchTransactionStatus(String transactionID) {
        MapSqlParameterSource param = new MapSqlParameterSource()
                .addValue(DBConstants.TRANSACTIONS_COL_TRANSACTION_ID, transactionID);
        List<TransactionEntity> transactionResponseList = namedParameterJdbcTemplate.query(SqlHandler.FETCH_TRANSACTION, param, transactionEntityRowMapper);
        if (transactionResponseList.isEmpty()) {
            return null;
        }
        return transactionResponseList.get(0);
    }

    @Override
    public TransactionPageResponse fetchTransactionHistory(String email, String pageToken, int limit, boolean reverse) {

        MapSqlParameterSource param = new MapSqlParameterSource()
                .addValue(DBConstants.TRANSACTIONS_COL_EMAIL, email);
        String nextToken = "";
        String decoded = "";

        if (pageToken.isEmpty()) {
            param
                    .addValue(DBConstants.TRANSACTIONS_COL_CREATED_TS, DBConstants.SMALLEST_TIMESTAMP)
                    .addValue(DBConstants.LIMIT, limit);
        }
        else {
            decoded = PaginationUtil.decode(pageToken);
            param
                    .addValue(DBConstants.TRANSACTIONS_COL_CREATED_TS, decoded)
                    .addValue(DBConstants.LIMIT, limit);
        }
        String sql = (reverse) ? SqlHandler.FETCH_ALL_TRANSACTIONS_REVERSE:SqlHandler.FETCH_ALL_TRANSACTIONS_FORWARD;
        List<TransactionEntity> transactionList = namedParameterJdbcTemplate.query(sql, param, transactionEntityRowMapper);
        if (!transactionList.isEmpty()) {
            nextToken = PaginationUtil.encode(transactionList.get(transactionList.size()-1).getCreatedTs().toString());
        }
        TransactionPageResponse.Builder transactionPageResponse = TransactionPageResponse.newBuilder();

        if (!(reverse && transactionList.size() < limit)) {
            transactionPageResponse.setHasPrev(true);
        }
        if(pageToken.isEmpty()) {
            transactionPageResponse.setHasPrev(false);
        }
        if (reverse) {
            String temp = pageToken;
            pageToken = nextToken;
            nextToken = temp;
        }
        transactionPageResponse.setReverseToken(pageToken);
        if (nextToken.isEmpty()) {
            transactionPageResponse.setToken("");
        } else {
            transactionPageResponse.setToken(nextToken);
        }
        if (reverse) {
            Collections.reverse(transactionList);
        }
        transactionPageResponse.addAllItems(transactionList);
        transactionPageResponse.setHasNext(reverse || transactionList.size() >= limit);
        return transactionPageResponse.build();

    }

}
