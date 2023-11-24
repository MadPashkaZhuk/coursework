package com.zhuk.hospital.enums;

import lombok.Getter;

@Getter
public enum ApiMessageEnum {
    USER_ALREADY_EXISTS("security.user.already-exists"),
    USER_NOT_FOUND("security.user.not-found");
    private final String code;
    ApiMessageEnum(String code) {
        this.code = code;
    }
}
