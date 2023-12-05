package com.zhuk.hospital.exception.medication;

import com.zhuk.hospital.exception.BaseApiException;
import org.springframework.http.HttpStatus;

public class MedicationNotFoundException extends BaseApiException {
    public MedicationNotFoundException(HttpStatus status, String message, int errorCode) {
        super(status, message, errorCode);
    }
}
