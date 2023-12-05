package com.zhuk.medication.utils;

import com.zhuk.medication.enums.ErrorCodeEnum;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
public class ErrorCodeHelper {
    public int getCode(ErrorCodeEnum codeEnum) {
        return codeEnum.getCode();
    }
}
