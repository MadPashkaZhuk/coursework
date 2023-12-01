package com.zhuk.hospital.service;

import com.zhuk.hospital.dto.DepartmentDto;
import com.zhuk.hospital.dto.NewTaskDto;
import com.zhuk.hospital.dto.TaskDto;
import com.zhuk.hospital.dto.UserDto;
import com.zhuk.hospital.entity.DepartmentEntity;
import com.zhuk.hospital.entity.TaskEntity;
import com.zhuk.hospital.enums.ApiMessageEnum;
import com.zhuk.hospital.enums.ErrorCodeEnum;
import com.zhuk.hospital.exception.task.TaskNotFoundException;
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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
                .filter(task -> task.getDateTimeOfIssue().toLocalDate().equals(LocalDate.now()))
               .toList();
    }

    public TaskDto findById(UUID id) {
        return taskMapper.map(getEntityByIdOrThrowException(id))
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

    public List<TaskDto> save(NewTaskDto dto) {
        DepartmentEntity departmentEntity = departmentService.getEntityByIdOrThrowException(dto.getDepartmentId());
        List<TaskEntity> taskEntities = new ArrayList<>();
        for(int i = 0; i < dto.getAmountOfDays(); i++) {
            for(int j = 0; j < dto.getDateTimeOfIssue().size(); j++) {
                LocalDateTime dateTimeOfIssue = dto.getDateTimeOfIssue().get(j).plusDays(i);
                TaskEntity entity = taskRepository.save(
                        TaskEntity.builder()
                                .patient(dto.getPatient())
                                .medicationId(dto.getMedicationId())
                                .department(departmentEntity)
                                .dateTimeOfIssue(dateTimeOfIssue)
                                .build());
                taskEntities.add(entity);
            }
        }
        return taskEntities.stream()
                .map(taskMapper::map)
                .toList();
    }

    @Transactional
    public void delete(UUID id) {
        Optional<TaskEntity> optionalTask = getOptionalEntityById(id);
        optionalTask.ifPresent(taskRepository::delete);
    }

    private Optional<TaskEntity> getOptionalEntityById(UUID id) {
        return taskRepository.findById(id);
    }

    private TaskEntity getEntityByIdOrThrowException(UUID id) {
        return getOptionalEntityById(id).orElseThrow(
                () -> new TaskNotFoundException(HttpStatus.NOT_FOUND,
                        messageSourceWrapper.getMessageCode(ApiMessageEnum.TASK_NOT_FOUND),
                        errorCodeHelper.getCode(ErrorCodeEnum.TASK_NOT_FOUND_CODE))
        );
    }
}
