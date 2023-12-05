package com.zhuk.hospital.mapper;

import com.zhuk.hospital.dto.UserDto;
import com.zhuk.hospital.entity.UserEntity;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {DepartmentMapper.class})
public interface UserMapper {
    @Mapping(target = "departments", source = "userEntity.departments")
    UserDto map(UserEntity userEntity);

    @InheritInverseConfiguration
    @Mapping(target = "departments", ignore = true)
    UserEntity map(UserDto userDto);
}
