package com.zhuk.hospital.client;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "medication.api")
public class MedicationApiProperties {
    private String url;
    private String username;
    private String password;
}