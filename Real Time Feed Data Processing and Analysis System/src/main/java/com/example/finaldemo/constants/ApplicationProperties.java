package com.example.finaldemo.constants;

import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.stereotype.Component;

@Component
public class ApplicationProperties {
    private final String awsAccessKey;
    private final String awsSecretKey;
    private final String awsSessionToken;

    public String getAwsAccessKey() {
        return awsAccessKey;
    }

    public String getAwsSecretKey() {
        return awsSecretKey;
    }

    public String getAwsSessionToken() {
        return awsSessionToken;
    }

    private final String awsSqsName;
    private final String jdbcUrl;
    private final String dbUsername;
    private final String dbPassword;
    private final String jdbcDriver;

    public ApplicationProperties(ConfigurableEnvironment env) {
        this.awsAccessKey = System.getenv("AWS_ACCESS_KEY_ID");
        this.awsSecretKey = System.getenv("AWS_SECRET_ACCESS_KEY");
        this.awsSessionToken = System.getenv("AWS_SESSION_TOKEN");
        this.awsSqsName = System.getenv("AWS_SQS_NAME");
        this.jdbcUrl = System.getenv("JDBC_URL");
        this.dbUsername = System.getenv("MYSQLUSERNAME");
        this.dbPassword = System.getenv("MYSQLPASSWORD");
        this.jdbcDriver = env.getRequiredProperty("db.jdbcDriver");
    }

    public String getAwsSqsName() {
        return awsSqsName;
    }

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public String getDbUsername() {
        return dbUsername;
    }

    public String getDbPassword() {
        return dbPassword;
    }

    public String getJdbcDriver() {
        return jdbcDriver;
    }

}
