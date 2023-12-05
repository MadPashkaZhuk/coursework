package com.zhuk.coursework.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.zhuk.coursework.enums.MedicationTypeEnum;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class MedicationDto {
    Long id;
    String name;
    String manufacturer;
    @Enumerated(EnumType.STRING)
    MedicationTypeEnum type;
    int weight;
    int quantity;
    String additionalInfo;
}
