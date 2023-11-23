package com.zhuk.coursework.config;

import lombok.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Value
@ConfigurationProperties("spring.liquibase.parameters.admin")
public class LiquibaseConfig {
    String ADMIN_USERNAME;
    String ADMIN_PASSWORD;
}
