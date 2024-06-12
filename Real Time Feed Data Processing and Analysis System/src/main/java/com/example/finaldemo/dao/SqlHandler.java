package com.example.finaldemo.dao;

public class SqlHandler {
    //    user sql queries
    public static final String ALL_USER = """
            SELECT name, email, password, balance, created_ts, updated_ts
            FROM User;
            """;
    public static final String GET_USER = """
            SELECT name, email, balance
            FROM User
            WHERE email=:email;
            """;
    public static final String REGISTER_USER = """
            INSERT INTO User (name, email, password, balance)
            VALUES (:name,:email,:password,:balance);
            """;
    public static final String REGISTER_USER_ASSET = """
            INSERT INTO UserAsset(email,asset_code)
            SELECT :email ,asset_code FROM AssetCode;
            """;

    public static final String GET_USER_TOTAL_BALANCE = """
            SELECT balance FROM User WHERE email=:email;
            """;

    public static final String SET_TOTAL_USER_BALANCE = """
            UPDATE User
            SET balance=:balance,
            updated_ts=:updated_ts
            WHERE email=:email;
            """;

    //user asset queries
    public static final String GET_USERASSET = """
            SELECT UA.email,AC.asset_name,UA.quantity,UA.updated_ts
            FROM UserAsset UA JOIN AssetCode AC
            ON UA.asset_code=AC.asset_code
            WHERE email=:email;
            """;
    public static final String SET_USERASSET_METAL = """
            UPDATE UserAsset
            SET quantity=:quantity,
            updated_ts=:updated_ts
            WHERE email=:email AND asset_code=:asset_code;
            """;

    //asset sql queries
    public static final String FETCH_ASSET_NAME = """
            SELECT asset_name FROM AssetCode
             WHERE asset_code= :asset_code;
            """;
    public static final String FETCH_ALL_ASSET = """
            SELECT currency,date,asset_code,performance,value,weight_unit
            FROM Asset;
            """;
    public static final String FETCH_ALL_ASSET_OFFSET_PAGINATION = """
            SELECT currency,date,asset_code,performance,value,weight_unit
            FROM Asset
            order BY date LIMIT :LIMIT OFFSET :OFFSET;
            """;
    public static final String FETCH_ALL_ASSET_PAGE_FORWARD = """
            SELECT currency,date,asset_code,performance,value,weight_unit
            FROM Asset
            WHERE (
            date BETWEEN :from AND :to
            AND(
            date>:date
            OR (date=:date AND asset_code>:asset_code)
            OR (date=:date AND asset_code=:asset_code AND currency>:currency)
               )
            )
            ORDER BY date,asset_code,currency LIMIT :LIMIT;
            """;
    public static final String FETCH_ALL_ASSET_PAGE_REVERSE = """
            SELECT currency,date,asset_code,performance,value,weight_unit
            FROM Asset
            WHERE (
            date<:date
            OR (date=:date AND asset_code<:asset_code)
            OR (date=:date AND asset_code=:asset_code AND currency<:currency))
            ORDER BY date DESC LIMIT :LIMIT;
            """;
    public static final String LIVE_ASSET_DATA = """
            SELECT distinct currency,date,asset_code,performance,value,weight_unit
            FROM Asset
            WHERE  date IN(
            SELECT  MAX(date)
            FROM Asset
            GROUP BY asset_code,currency)
            AND asset_code=:asset_code AND currency=:currency;
            """;
    public static final String INSERT_ASSET_DATA = """
            INSERT IGNORE INTO Asset
            (date,currency,asset_code,performance,value,weight_unit)
            VALUES(:date,:currency,:asset_code,:performance,:value,:weight_unit);
            """;

    //transactions
    public static final String FETCH_ALL_TRANSACTIONS = """
            SELECT email, transaction_id, transaction_type, status, amount, asset_code, currency, quantity, created_ts, updated_ts
            FROM Transactions
            WHERE email=:email
            ORDER BY updated_ts;
            """;
    public static final String FETCH_ALL_TRANSACTIONS_FORWARD = """
            SELECT email, transaction_id, transaction_type, status, amount, asset_code, currency, quantity, created_ts, updated_ts
            FROM Transactions
            WHERE email=:email
            AND created_ts > :created_ts
            ORDER BY updated_ts
            LIMIT :LIMIT;
            """;
    public static final String FETCH_ALL_TRANSACTIONS_REVERSE = """
            SELECT email, transaction_id, transaction_type, status, amount, asset_code, currency, quantity, created_ts, updated_ts
            FROM Transactions
            WHERE email=:email
            AND created_ts < :created_ts
            ORDER BY updated_ts DESC
            LIMIT :LIMIT;
            """;
    public static final String UPDATE_TRANSACTION_STATUS = """
            UPDATE Transactions
            SET status=:status,
            updated_ts=:updated_ts
            WHERE transaction_id=:transaction_id;
            """;
    public static final String FETCH_TRANSACTION = """
            SELECT email, transaction_id, transaction_type, status, amount, asset_code, currency, quantity, created_ts, updated_ts
            FROM Transactions
            WHERE transaction_id=:transaction_id
            ORDER BY updated_ts;
            """;
    public static final String INSERT_TRANSACTION = """
            INSERT INTO Transactions
            (email, transaction_id, transaction_type, status, amount, asset_code, currency, quantity)
            VALUES (:email,:transaction_id,:transaction_type,:status,:amount,:asset_code,:currency,:quantity);
            """;

    private SqlHandler() {
    }
}
