package com.zhuk.medication.mapper;

import com.zhuk.medication.dto.UserDto;
import com.zhuk.medication.entity.UserEntity;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {MedicationMapper.class})
public interface UserMapper {
    UserDto map(UserEntity userEntity);

    @InheritInverseConfiguration
    UserEntity map(UserDto userDto);
}
