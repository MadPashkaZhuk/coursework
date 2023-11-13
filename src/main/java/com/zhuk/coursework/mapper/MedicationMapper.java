package com.zhuk.coursework.mapper;

import com.zhuk.coursework.dto.MedicationDto;
import com.zhuk.coursework.entity.MedicationEntity;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MedicationMapper {
    MedicationDto map(MedicationEntity medicationEntity);

    @InheritInverseConfiguration
    MedicationEntity map(MedicationDto medicationDto);
}
