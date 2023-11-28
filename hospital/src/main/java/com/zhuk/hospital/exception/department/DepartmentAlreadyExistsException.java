package com.zhuk.hospital.exception.department;

import com.zhuk.hospital.exception.BaseApiException;
import org.springframework.http.HttpStatus;

public class DepartmentAlreadyExistsException extends BaseApiException {
    public DepartmentAlreadyExistsException(HttpStatus status, String message, int errorCode) {
        super(status, message, errorCode);
    }
}
