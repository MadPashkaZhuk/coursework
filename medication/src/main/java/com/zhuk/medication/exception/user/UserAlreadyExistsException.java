package com.zhuk.medication.exception.user;

import com.zhuk.medication.exception.BaseApiException;
import org.springframework.http.HttpStatus;

public class UserAlreadyExistsException extends BaseApiException {
    public UserAlreadyExistsException(HttpStatus status, String message, int errorCode) {
        super(status, message, errorCode);
    }
}
