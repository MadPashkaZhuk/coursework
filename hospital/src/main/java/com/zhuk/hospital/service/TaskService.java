package com.zhuk.hospital.service;

import com.zhuk.hospital.client.MedicationRestClient;
import com.zhuk.hospital.dto.DepartmentDto;
import com.zhuk.hospital.dto.NewTaskDto;
import com.zhuk.hospital.dto.TaskDto;
import com.zhuk.hospital.dto.UserDto;
import com.zhuk.hospital.entity.DepartmentEntity;
import com.zhuk.hospital.entity.TaskEntity;
import com.zhuk.hospital.enums.ApiMessageEnum;
import com.zhuk.hospital.enums.ErrorCodeEnum;
import com.zhuk.hospital.enums.UserRoleEnum;
import com.zhuk.hospital.exception.task.TaskNotAllowedException;
import com.zhuk.hospital.exception.task.TaskNotFoundException;
import com.zhuk.hospital.exception.task.TaskOutdatedException;
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
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.IntStream;

@Component
@RequiredArgsConstructor
public class TaskService {
    private final MessageSourceWrapper messageSourceWrapper;
    private final ErrorCodeHelper errorCodeHelper;
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final UserService userService;
    private final DepartmentService departmentService;
    private final MedicationRestClient medicationRestClient;

    public List<TaskDto> findAll() {
        List<DepartmentDto> departments = getDepartmentsListForCurrentUser();
        return departments.stream()
                .flatMap(departmentDto -> departmentDto.getTasks().stream())
                .filter(task -> task.getDateTimeOfIssue().toLocalDate().equals(LocalDate.now()))
               .toList();
    }

    public TaskDto findById(UUID id) {
        return taskMapper.map(getEntityByIdOrThrowException(id));
    }

    private List<DepartmentDto> getDepartmentsListForCurrentUser() {
        UserDto user = userService.findUserByUsername(getCurrentUser().getUsername());
        if(user.getRole() == UserRoleEnum.ROLE_ADMIN) {
            return departmentService.findAll();
        }
        return user.getDepartments();
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

    @Transactional
    public List<TaskDto> save(NewTaskDto dto) {
        validateNewDto(dto);
        int quantity = dto.getAmountOfDays() * dto.getTimeOfIssuing().size();
        medicationRestClient.reduceQuantity(dto.getMedicationId(), quantity);
        List<TaskEntity> taskEntities = getTaskEntityListFromDto(dto);
        return taskEntities.stream()
                .map(taskMapper::map)
                .toList();
    }

    private List<TaskEntity> getTaskEntityListFromDto(NewTaskDto dto) {
        DepartmentEntity departmentEntity = departmentService.getEntityByIdOrThrowException(dto.getDepartmentId());
        return IntStream.range(0, dto.getAmountOfDays())
                .boxed()
                .flatMap(i -> dto.getTimeOfIssuing().stream()
                        .map(time -> LocalDateTime.of(dto.getStartDay(), time).plusDays(i))
                        .map(dateTimeOfIssue -> TaskEntity.builder()
                                .patient(dto.getPatient())
                                .medicationId(dto.getMedicationId())
                                .department(departmentEntity)
                                .dateTimeOfIssue(dateTimeOfIssue)
                                .build())
                        .map(taskRepository::save))
                .toList();
    }
    private void validateNewDto(NewTaskDto dto) {
        if(dto.getStartDay().isBefore(LocalDate.now())) {
            throw new TaskOutdatedException(
                    HttpStatus.BAD_REQUEST,
                    messageSourceWrapper.getMessageCode(ApiMessageEnum.TASK_OUTDATED),
                    errorCodeHelper.getCode(ErrorCodeEnum.TASK_OUTDATED_CODE)
            );
        }
        if(departmentIdIsNotValidated(dto.getDepartmentId())) {
            throw new TaskNotAllowedException(
                    HttpStatus.FORBIDDEN,
                    messageSourceWrapper.getMessageCode(ApiMessageEnum.TASK_NOT_ALLOWED),
                    errorCodeHelper.getCode(ErrorCodeEnum.TASK_NOT_ALLOWED_CODE)
            );
        }
    }

    @Transactional
    public void delete(UUID id) {
        Optional<TaskEntity> optionalTask = getOptionalEntityById(id);
        optionalTask.ifPresent(taskEntity -> {
            if(departmentIdIsNotValidated(taskEntity.getDepartment().getId())) {
                return;
            }
            if(taskEntity.getDateTimeOfIssue().isAfter(LocalDateTime.now())) {
                medicationRestClient.increaseQuantity(taskEntity.getMedicationId(), 1);
            }
            taskRepository.delete(taskEntity);
        });
    }
    private boolean departmentIdIsNotValidated(Long id) {
        return getDepartmentsListForCurrentUser().stream()
                .noneMatch(x -> x.getId().equals(id));
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
