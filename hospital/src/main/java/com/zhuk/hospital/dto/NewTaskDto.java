package com.zhuk.hospital.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Value
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class NewTaskDto {
    String patient;
    Long medicationId;
    LocalDate startDay;
    List<LocalTime> timeOfIssuing;
    Long departmentId;
    int amountOfDays;
}
