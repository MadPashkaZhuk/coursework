package com.zhuk.hospital.exception.medication;

import com.zhuk.hospital.exception.BaseApiException;
import org.springframework.http.HttpStatus;

public class MedicationUnauthorizedException extends BaseApiException {
    public MedicationUnauthorizedException(HttpStatus status, String message, int errorCode) {
        super(status, message, errorCode);
    }
}
