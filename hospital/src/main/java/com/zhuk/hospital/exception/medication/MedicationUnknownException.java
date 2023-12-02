package com.zhuk.hospital.exception.medication;

import com.zhuk.hospital.exception.BaseApiException;
import org.springframework.http.HttpStatus;

public class MedicationUnknownException extends BaseApiException {
    public MedicationUnknownException(HttpStatus status, String message, int errorCode) {
        super(status, message, errorCode);
    }
}
