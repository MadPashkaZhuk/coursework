package com.zhuk.coursework.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Value;

@Value
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class NewMedicationDto {
    String name;
    String manufacturer;
    String type;
    int weight;
    boolean requirePrescription;
    String additionalInfo;
}
