package com.zhuk.medication.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Value;

@Value
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Builder(toBuilder = true)
public class NewMedicationDto {
    String name;
    String manufacturer;
    String type;
    int weight;
    int quantity;
    String additionalInfo;
}
