package com.zhuk.medication.config;

import lombok.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Value
@ConfigurationProperties("spring.datasource")
public class AppConfig {
    String DB_URL;
    String DB_USERNAME;
    String DB_PASSWORD;
}
