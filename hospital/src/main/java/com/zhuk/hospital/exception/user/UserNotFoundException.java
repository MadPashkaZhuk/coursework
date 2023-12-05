package com.zhuk.hospital.exception.user;

import com.zhuk.hospital.exception.BaseApiException;
import org.springframework.http.HttpStatus;

public class UserNotFoundException extends BaseApiException {
    public UserNotFoundException(HttpStatus status, String message, int errorCode) {
        super(status, message, errorCode);
    }
}
