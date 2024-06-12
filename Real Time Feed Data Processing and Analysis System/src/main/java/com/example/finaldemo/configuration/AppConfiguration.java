package com.example.finaldemo.configuration;

import com.example.finaldemo.constants.ApplicationProperties;
import com.example.finaldemo.constants.ResourceConstants;
import com.example.finaldemo.proto.AssetDTO;
import com.example.finaldemo.proto.AssetDtoResponse;
import com.example.finaldemo.utility.ProtoMessageConverter;
import com.google.protobuf.util.JsonFormat;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.protobuf.ProtobufHttpMessageConverter;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;
@Configuration
public class AppConfiguration {
    private final ApplicationProperties applicationProperties;

    public AppConfiguration(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    @Bean
    ProtobufHttpMessageConverter protobufHttpMessageConverter() {
        return new ProtobufHttpMessageConverter();
    }
    @Bean
    ProtoMessageConverter getMessageConverter() {
        JsonFormat.TypeRegistry typeRegistry = JsonFormat.TypeRegistry.newBuilder()
                .add(AssetDTO.getDescriptor())
                .add(AssetDtoResponse.getDescriptor())
                .build();
        return new ProtoMessageConverter(typeRegistry);
    }
    @Bean(ResourceConstants.BEAN_MYSQL_DATASOURCE)
    public HikariDataSource mysqlDataSource() {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName(applicationProperties.getJdbcDriver());
        hikariConfig.setJdbcUrl(applicationProperties.getJdbcUrl());
        hikariConfig.setUsername(applicationProperties.getDbUsername());
        hikariConfig.setPassword(applicationProperties.getDbPassword());
        hikariConfig.setMaximumPoolSize(1);
        return new HikariDataSource(hikariConfig);
    }
    @Bean(ResourceConstants.BEAN_JDBC_TEMPLATE)
    public NamedParameterJdbcTemplate mySqlNamedParameterJdbcTemplate(@Qualifier(ResourceConstants.BEAN_MYSQL_DATASOURCE) DataSource dataSource){
        return new NamedParameterJdbcTemplate(dataSource);
    }

}
