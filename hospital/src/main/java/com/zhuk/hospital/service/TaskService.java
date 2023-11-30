package com.zhuk.hospital.service;

import com.zhuk.hospital.dto.DepartmentDto;
import com.zhuk.hospital.dto.NewTaskDto;
import com.zhuk.hospital.dto.TaskDto;
import com.zhuk.hospital.dto.UserDto;
import com.zhuk.hospital.entity.DepartmentEntity;
import com.zhuk.hospital.entity.TaskEntity;
import com.zhuk.hospital.enums.ApiMessageEnum;
import com.zhuk.hospital.enums.ErrorCodeEnum;
import com.zhuk.hospital.exception.user.UserUnknownException;
import com.zhuk.hospital.mapper.TaskMapper;
import com.zhuk.hospital.repository.TaskRepository;
import com.zhuk.hospital.security.CustomUserDetails;
import com.zhuk.hospital.utils.ErrorCodeHelper;
import com.zhuk.hospital.utils.MessageSourceWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TaskService {
    private final MessageSourceWrapper messageSourceWrapper;
    private final ErrorCodeHelper errorCodeHelper;
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final UserService userService;
    private final DepartmentService departmentService;

    public List<TaskDto> findAll() {
        UserDto user = userService.findUserByUsername(getCurrentUsername());
        List<DepartmentDto> departments = user.getDepartments();
        return departments.stream()
                .flatMap(departmentDto -> departmentDto.getTasks().stream())
               .toList();
    }

    private String getCurrentUsername() {
        return getCurrentUser().getUsername();
    }

    private CustomUserDetails getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails)) {
            throw new UserUnknownException(HttpStatus.FORBIDDEN,
                    messageSourceWrapper.getMessageCode(ApiMessageEnum.USER_UNKNOWN_EXCEPTION),
                    errorCodeHelper.getCode(ErrorCodeEnum.USER_UNKNOWN_EXCEPTION_CODE));
        }
        return ((CustomUserDetails) authentication.getPrincipal());
    }

    public TaskDto save(NewTaskDto dto) {
        DepartmentEntity departmentEntity = departmentService.getEntityByIdOrThrowException(dto.getDepartmentId());
        TaskEntity entity = taskRepository.save(
                TaskEntity.builder()
                        .patient(dto.getPatient())
                        .medicationId(dto.getMedicationId())
                        .department(departmentEntity)
                        .dateTimeOfIssue(dto.getDateTimeOfIssue())
                        .build());
        return taskMapper.map(entity);
    }
}
