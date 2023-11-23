package com.zhuk.coursework.utils;

import com.zhuk.coursework.enums.ErrorCodeEnum;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
public class ErrorCodeHelper {
    public int getCode(ErrorCodeEnum codeEnum) {
        return codeEnum.getCode();
    }
}
