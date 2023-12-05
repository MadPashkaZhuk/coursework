package com.zhuk.medication.exception.medication;

import com.zhuk.medication.exception.BaseApiException;
import org.springframework.http.HttpStatus;

public class MedicationNoRightsException extends BaseApiException {
    public MedicationNoRightsException(HttpStatus status, String message, int errorCode) {
        super(status, message, errorCode);
    }
}
