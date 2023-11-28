package com.zhuk.hospital.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class NewDepartmentDto {
    String name;
    String description;
}
