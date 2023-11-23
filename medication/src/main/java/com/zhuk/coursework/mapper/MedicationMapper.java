package com.zhuk.coursework.mapper;

import com.zhuk.coursework.dto.MedicationDto;
import com.zhuk.coursework.dto.NewMedicationDto;
import com.zhuk.coursework.entity.MedicationEntity;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MedicationMapper {
    MedicationDto map(MedicationEntity medicationEntity);

    @InheritInverseConfiguration
    MedicationEntity map(MedicationDto medicationDto);

    @Mapping(target = "id", ignore = true)
    MedicationEntity mapFromNewDto(NewMedicationDto newMedicationDto);
}
