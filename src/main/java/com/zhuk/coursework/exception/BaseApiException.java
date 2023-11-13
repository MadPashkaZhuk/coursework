package com.zhuk.coursework.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BaseApiException extends RuntimeException {
    private final HttpStatus status;
    private final String message;
    private final int errorCode;
    public BaseApiException (HttpStatus status, String message, int errorCode) {
        super(message);
        this.message = message;
        this.status = status;
        this.errorCode = errorCode;
    }
}
