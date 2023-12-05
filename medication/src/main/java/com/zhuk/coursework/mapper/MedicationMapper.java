package com.zhuk.coursework.mapper;

import com.zhuk.coursework.dto.MedicationDto;
import com.zhuk.coursework.entity.MedicationEntity;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MedicationMapper {
    MedicationDto map(MedicationEntity medicationEntity);

    @InheritInverseConfiguration
    MedicationEntity map(MedicationDto medicationDto);
}
