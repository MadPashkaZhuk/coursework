package com.zhuk.coursework.dto;

import lombok.Value;

@Value
public class NewMedicationDto {
    String name;
    String manufacturer;
    String type;
    int weight;
    boolean requirePrescription;
    String additionalInfo;
}
