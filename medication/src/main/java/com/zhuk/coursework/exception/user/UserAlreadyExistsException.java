package com.zhuk.coursework.exception.user;

import com.zhuk.coursework.exception.BaseApiException;
import org.springframework.http.HttpStatus;

public class UserAlreadyExistsException extends BaseApiException {
    public UserAlreadyExistsException(HttpStatus status, String message, int errorCode) {
        super(status, message, errorCode);
    }
}
