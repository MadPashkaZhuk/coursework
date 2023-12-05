package com.zhuk.hospital.mapper;

import com.zhuk.hospital.dto.TaskDto;
import com.zhuk.hospital.entity.TaskEntity;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TaskMapper {
    TaskDto map(TaskEntity taskEntity);

    @InheritInverseConfiguration
    TaskEntity map(TaskDto taskDto);
}
