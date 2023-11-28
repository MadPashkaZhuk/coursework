package com.zhuk.hospital.mapper;

import com.zhuk.hospital.dto.DepartmentDto;
import com.zhuk.hospital.entity.DepartmentEntity;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")

public interface DepartmentMapper {
    DepartmentDto map(DepartmentEntity departmentEntity);

    @Mapping(target = "users", ignore = true)
    @InheritInverseConfiguration
    DepartmentEntity map(DepartmentDto departmentDto);
}
