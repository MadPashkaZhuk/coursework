package com.zhuk.coursework.dto;

import lombok.Value;

@Value
public class CredentialsDto {
    String username;
    char[] password;
}
