package com.zhuk.hospital.enums;

import lombok.Getter;

@Getter
public enum ApiMessageEnum {
    USER_ALREADY_EXISTS("security.user.already-exists"),
    USER_NOT_FOUND("security.user.not-found"),
    USER_UNKNOWN_EXCEPTION("security.user.unknown-exception"),
    DEPARTMENT_NOT_FOUND("api.department.not-found"),
    DEPARTMENT_ALREADY_EXISTS("api.department.already-exists"),
    TASK_NOT_FOUND("api.task.not-found"),
    TASK_OUTDATED("api.task.outdated");
    private final String code;
    ApiMessageEnum(String code) {
        this.code = code;
    }
}
