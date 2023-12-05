package com.zhuk.medication.mapper;

import com.zhuk.medication.dto.MedicationDto;
import com.zhuk.medication.entity.MedicationEntity;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MedicationMapper {
    MedicationDto map(MedicationEntity medicationEntity);

    @InheritInverseConfiguration
    MedicationEntity map(MedicationDto medicationDto);
}
