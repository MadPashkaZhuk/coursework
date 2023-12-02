package com.zhuk.hospital.exception.medication;

import com.zhuk.hospital.exception.BaseApiException;
import org.springframework.http.HttpStatus;

public class MedicationForbiddenException extends BaseApiException {
    public MedicationForbiddenException(HttpStatus status, String message, int errorCode) {
        super(status, message, errorCode);
    }
}
