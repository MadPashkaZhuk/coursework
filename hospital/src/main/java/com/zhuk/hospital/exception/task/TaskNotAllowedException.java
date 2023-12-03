package com.zhuk.hospital.exception.task;

import com.zhuk.hospital.exception.BaseApiException;
import org.springframework.http.HttpStatus;

public class TaskNotAllowedException extends BaseApiException {
    public TaskNotAllowedException(HttpStatus status, String message, int errorCode) {
        super(status, message, errorCode);
    }
}
