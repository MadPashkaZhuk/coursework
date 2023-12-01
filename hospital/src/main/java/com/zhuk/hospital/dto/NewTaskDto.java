package com.zhuk.hospital.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.List;

@Value
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class NewTaskDto {
    String patient;
    Long medicationId;
    List<LocalDateTime> dateTimeOfIssue;
    Long departmentId;
    int amountOfDays;
}
