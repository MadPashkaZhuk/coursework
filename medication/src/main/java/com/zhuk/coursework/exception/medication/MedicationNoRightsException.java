package com.zhuk.coursework.exception.medication;

import com.zhuk.coursework.exception.BaseApiException;
import org.springframework.http.HttpStatus;

public class MedicationNoRightsException extends BaseApiException {
    public MedicationNoRightsException(HttpStatus status, String message, int errorCode) {
        super(status, message, errorCode);
    }
}
