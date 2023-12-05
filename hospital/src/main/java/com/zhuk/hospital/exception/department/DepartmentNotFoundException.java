package com.zhuk.hospital.exception.department;

import com.zhuk.hospital.exception.BaseApiException;
import org.springframework.http.HttpStatus;

public class DepartmentNotFoundException extends BaseApiException {
    public DepartmentNotFoundException(HttpStatus status, String message, int errorCode) {
        super(status, message, errorCode);
    }
}
