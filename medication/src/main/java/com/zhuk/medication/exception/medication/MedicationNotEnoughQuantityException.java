package com.zhuk.medication.exception.medication;

import com.zhuk.medication.exception.BaseApiException;
import org.springframework.http.HttpStatus;

public class MedicationNotEnoughQuantityException extends BaseApiException {
    public MedicationNotEnoughQuantityException(HttpStatus status, String message, int errorCode) {
        super(status, message, errorCode);
    }
}
