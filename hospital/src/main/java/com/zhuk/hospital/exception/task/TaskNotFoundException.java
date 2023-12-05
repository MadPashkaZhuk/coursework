package com.zhuk.hospital.exception.task;

import com.zhuk.hospital.exception.BaseApiException;
import org.springframework.http.HttpStatus;

public class TaskNotFoundException extends BaseApiException {
    public TaskNotFoundException(HttpStatus status, String message, int errorCode) {
        super(status, message, errorCode);
    }
}
