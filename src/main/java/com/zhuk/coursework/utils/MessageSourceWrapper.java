package com.zhuk.coursework.utils;

import com.zhuk.coursework.enums.ApiMessageEnum;
import lombok.AllArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
@AllArgsConstructor
public class MessageSourceWrapper {
    private final MessageSource messageSource;
    public String getMessageCode(ApiMessageEnum apiMessageEnum) {
        return messageSource.getMessage(apiMessageEnum.getCode(), null, Locale.getDefault());
    }
}
