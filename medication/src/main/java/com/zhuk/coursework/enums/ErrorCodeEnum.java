package com.zhuk.coursework.enums;

import lombok.Getter;

@Getter
public enum ErrorCodeEnum {
    MEDICATION_NOT_FOUND(7001),
    MEDICATION_ALREADY_EXISTS(7002),
    USER_NOT_FOUND_CODE(8001),
    USER_ALREADY_EXISTS_CODE(8002),
    UNKNOWN_ERROR_CODE(9999);
    private final int code;
    ErrorCodeEnum(int code) {
        this.code = code;
    }
}
