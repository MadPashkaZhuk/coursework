package com.zhuk.coursework.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class CredentialsDto {
    String username;
    char[] password;
}
