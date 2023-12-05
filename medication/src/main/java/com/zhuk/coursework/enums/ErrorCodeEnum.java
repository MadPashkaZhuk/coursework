package com.zhuk.coursework.enums;

import lombok.Getter;

@Getter
public enum ErrorCodeEnum {
    MEDICATION_NOT_FOUND_CODE(7001),
    MEDICATION_ALREADY_EXISTS_CODE(7002),
    MEDICATION_NOT_ENOUGH_QUANTITY_CODE(7003),
    MEDICATION_NO_RIGHTS_CODE(7004),
    USER_NOT_FOUND_CODE(8001),
    USER_ALREADY_EXISTS_CODE(8002),
    USER_UNKNOWN_EXCEPTION_CODE(8003),
    UNKNOWN_ERROR_CODE(9999);
    private final int code;
    ErrorCodeEnum(int code) {
        this.code = code;
    }
}
