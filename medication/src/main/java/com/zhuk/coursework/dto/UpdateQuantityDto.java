package com.zhuk.coursework.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Builder(toBuilder = true)
@Jacksonized
public class UpdateQuantityDto {
    Integer quantity;
}
