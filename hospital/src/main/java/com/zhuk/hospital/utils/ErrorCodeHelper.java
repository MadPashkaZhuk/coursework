package com.zhuk.hospital.utils;

import com.zhuk.hospital.enums.ErrorCodeEnum;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
public class ErrorCodeHelper {
    public int getCode(ErrorCodeEnum codeEnum) {
        return codeEnum.getCode();
    }
}
