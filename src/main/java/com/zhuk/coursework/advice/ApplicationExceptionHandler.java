package com.zhuk.coursework.advice;

import com.zhuk.coursework.dto.ExceptionDto;
import com.zhuk.coursework.enums.ApiMessageEnum;
import com.zhuk.coursework.enums.ErrorCodeEnum;
import com.zhuk.coursework.exception.BaseApiException;
import com.zhuk.coursework.utils.ErrorCodeHelper;
import com.zhuk.coursework.utils.MessageSourceWrapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ApplicationExceptionHandler {
    private final ErrorCodeHelper errorCodeHelper;
    private final MessageSourceWrapper messageSourceWrapper;
    public ApplicationExceptionHandler(ErrorCodeHelper errorCodeHelper, MessageSourceWrapper messageSourceWrapper) {
        this.errorCodeHelper = errorCodeHelper;
        this.messageSourceWrapper = messageSourceWrapper;
    }

    @ExceptionHandler(BaseApiException.class)
    public ResponseEntity<ExceptionDto> handleBaseApiException(BaseApiException baseApiException) {
        ExceptionDto exceptionDto = new ExceptionDto(baseApiException.getStatus(),
                baseApiException.getMessage(),
                baseApiException.getErrorCode());
        return new ResponseEntity<>(exceptionDto, baseApiException.getStatus());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ExceptionDto> handleDataIntegrityViolation(DataIntegrityViolationException exception) {
        ExceptionDto exceptionDto =
                new ExceptionDto(HttpStatus.CONFLICT,
                        messageSourceWrapper.getMessageCode(ApiMessageEnum.DATA_VIOLATION),
                        errorCodeHelper.getCode(ErrorCodeEnum.DATA_VIOLATION_CODE));
        return new ResponseEntity<>(exceptionDto, HttpStatus.CONFLICT);
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
        System.out.println(exception.getClass());
        ExceptionDto exceptionDto =
                new ExceptionDto(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage(),
                        errorCodeHelper.getCode(ErrorCodeEnum.UNKNOWN_ERROR_CODE));
        return new ResponseEntity<>(exceptionDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

