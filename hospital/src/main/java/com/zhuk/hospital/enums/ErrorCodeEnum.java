package com.zhuk.hospital.enums;

import lombok.Getter;

@Getter
public enum ErrorCodeEnum {
    TASK_NOT_FOUND_CODE(6001),
    TASK_OUTDATED_CODE(6002),
    DEPARTMENT_NOT_FOUND_CODE(7001),
    DEPARTMENT_ALREADY_EXISTS(7002),
    USER_NOT_FOUND_CODE(8001),
    USER_ALREADY_EXISTS_CODE(8002),
    USER_UNKNOWN_EXCEPTION_CODE(8003),
    UNKNOWN_ERROR_CODE(9999);
    private final int code;
    ErrorCodeEnum(int code) {
        this.code = code;
    }
}
