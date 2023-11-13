package com.zhuk.coursework.exception.medication;

import com.zhuk.coursework.exception.BaseApiException;
import org.springframework.http.HttpStatus;

public class MedicationNotFoundException extends BaseApiException {
    public MedicationNotFoundException(HttpStatus status, String message, int errorCode) {
        super(status, message, errorCode);
    }
}
