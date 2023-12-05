package com.zhuk.medication.exception.user;

import com.zhuk.medication.exception.BaseApiException;
import org.springframework.http.HttpStatus;

public class UserNotFoundException extends BaseApiException {
    public UserNotFoundException(HttpStatus status, String message, int errorCode) {
        super(status, message, errorCode);
    }
}
