package com.zhuk.coursework.enums;

import lombok.Getter;

@Getter
public enum ApiMessageEnum {
    MEDICATION_NOT_FOUND("medication.not-found"),
    MEDICATION_ALREADY_EXISTS("medication.already-exists"),
    MEDICATION_NOT_ENOUGH_QUANTITY("medication.not-enough-quantity"),
    MEDICATION_NO_RIGHTS("medication.no-rights"),
    USER_ALREADY_EXISTS("security.user.already-exists"),
    USER_NOT_FOUND("security.user.not-found"),
    USER_UNKNOWN_EXCEPTION("security.user.unknown-exception");
    private final String code;
    ApiMessageEnum(String code) {
        this.code = code;
    }
}
