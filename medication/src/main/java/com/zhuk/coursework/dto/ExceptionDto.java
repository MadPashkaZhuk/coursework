package com.zhuk.coursework.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Value;
import org.springframework.http.HttpStatus;

@Value
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ExceptionDto {
    HttpStatus status;
    String exceptionMessage;
    int code;
}
