package com.zhuk.hospital.mapper;

import com.zhuk.hospital.dto.DepartmentDto;
import com.zhuk.hospital.entity.DepartmentEntity;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {TaskMapper.class})
public interface DepartmentMapper {
    DepartmentDto map(DepartmentEntity departmentEntity);

    @InheritInverseConfiguration
    DepartmentEntity map(DepartmentDto departmentDto);
}
