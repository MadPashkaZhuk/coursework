package com.zhuk.coursework.enums;

import lombok.Getter;

@Getter
public enum ApiMessageEnum {
    MEDICATION_NOT_FOUND("medication.not-found"),
    MEDICATION_ALREADY_EXISTS("medication.already-exists"),
    NOT_ENOUGH_QUANTITY("medication.not-enough-quantity"),
    USER_ALREADY_EXISTS("security.user.already-exists"),
    USER_NOT_FOUND("security.user.not-found");
    private final String code;
    ApiMessageEnum(String code) {
        this.code = code;
    }
}
