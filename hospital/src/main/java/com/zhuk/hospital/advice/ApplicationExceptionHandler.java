package com.zhuk.hospital.advice;

import com.zhuk.hospital.dto.ExceptionDto;
import com.zhuk.hospital.enums.ErrorCodeEnum;
import com.zhuk.hospital.exception.BaseApiException;
import com.zhuk.hospital.utils.ErrorCodeHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@RequiredArgsConstructor
public class ApplicationExceptionHandler {
    private final ErrorCodeHelper errorCodeHelper;

    @ExceptionHandler(BaseApiException.class)
    public ResponseEntity<ExceptionDto> handleBaseApiException(BaseApiException baseApiException) {
        ExceptionDto exceptionDto = ExceptionDto.builder()
                .status(baseApiException.getStatus())
                .exceptionMessage(baseApiException.getMessage())
                .code(baseApiException.getErrorCode())
                .build();
        return new ResponseEntity<>(exceptionDto, baseApiException.getStatus());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleInvalidArgument(MethodArgumentNotValidException exception) {
        Map<String, String> errors = new HashMap<>();
        exception.getBindingResult().getFieldErrors().forEach(x -> errors.put(x.getField(), x.getDefaultMessage()));
        return errors;
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ExceptionDto> handleUnexpectedExceptions(Throwable exception) {
        ExceptionDto exceptionDto = ExceptionDto.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .exceptionMessage(exception.getMessage())
                .code(errorCodeHelper.getCode(ErrorCodeEnum.UNKNOWN_ERROR_CODE))
                .build();
        return new ResponseEntity<>(exceptionDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
