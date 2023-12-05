package com.zhuk.medication.exception.medication;

import com.zhuk.medication.exception.BaseApiException;
import org.springframework.http.HttpStatus;

public class MedicationNotFoundException extends BaseApiException {
    public MedicationNotFoundException(HttpStatus status, String message, int errorCode) {
        super(status, message, errorCode);
    }
}
