package com.zhuk.hospital.mapper;

import com.zhuk.hospital.dto.DepartmentDto;
import com.zhuk.hospital.entity.DepartmentEntity;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")

public interface DepartmentMapper {
    DepartmentDto map(DepartmentEntity departmentEntity);

    @InheritInverseConfiguration
    DepartmentEntity map(DepartmentDto departmentDto);
}
