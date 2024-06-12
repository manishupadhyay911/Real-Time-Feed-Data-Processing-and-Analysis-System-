package com.example.finaldemo.configuration;

public class SQLHandler {
    public static final String USER_BY_USERNAME_QUERY = """
            SELECT email AS username, password, 'true' AS enabled FROM User
            WHERE email = ?;
            """;
    public static final String AUTHORITY_BY_USERNAME_QUERY = """
            SELECT email AS username, 'ROLE_USER' AS authority FROM User
            WHERE email = ?;
            """;
    private SQLHandler() {
    }

}
