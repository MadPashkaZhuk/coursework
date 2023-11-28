package com.zhuk.hospital.service;

import com.zhuk.hospital.dto.DepartmentDto;
import com.zhuk.hospital.dto.UserDepartmentAssociationDTO;
import com.zhuk.hospital.entity.DepartmentEntity;
import com.zhuk.hospital.entity.UserEntity;
import com.zhuk.hospital.enums.ApiMessageEnum;
import com.zhuk.hospital.enums.ErrorCodeEnum;
import com.zhuk.hospital.exception.department.DepartmentNotFoundException;
import com.zhuk.hospital.exception.user.UserUnknownException;
import com.zhuk.hospital.mapper.DepartmentMapper;
import com.zhuk.hospital.mapper.UserMapper;
import com.zhuk.hospital.repository.DepartmentRepository;
import com.zhuk.hospital.security.CustomUserDetails;
import com.zhuk.hospital.utils.ErrorCodeHelper;
import com.zhuk.hospital.utils.MessageSourceWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DepartmentService {
    private final MessageSourceWrapper messageSourceWrapper;
    private final ErrorCodeHelper errorCodeHelper;
    private final DepartmentRepository departmentRepository;
    private final UserMapper userMapper;
    private final UserService userService;
    private final DepartmentMapper departmentMapper;

    public List<DepartmentDto> findAll() {
        return departmentRepository.findAll()
                .stream().map(departmentMapper::map)
                .toList();
    }

    @Transactional
    public void addUserToDepartment(UserDepartmentAssociationDTO dto) {
        DepartmentEntity departmentEntity = getEntityByIdOrThrowException(dto.getDepartmentId());
        UserEntity userEntity = userService.getUserEntityByUsernameOrThrowException(dto.getUsername());
        departmentEntity.getUsers().add(userEntity);
        userService.addDepartmentByUsername(dto.getUsername(), departmentEntity);
        departmentRepository.save(departmentEntity);
    }

    private DepartmentEntity getEntityByIdOrThrowException(Long id) {
        Optional<DepartmentEntity> entity = getOptionalEntityById(id);
        return entity.orElseThrow(() -> new DepartmentNotFoundException(HttpStatus.NOT_FOUND,
                messageSourceWrapper.getMessageCode(ApiMessageEnum.DEPARTMENT_NOT_FOUND),
                errorCodeHelper.getCode(ErrorCodeEnum.DEPARTMENT_NOT_FOUND_CODE)));
    }

    private Optional<DepartmentEntity> getOptionalEntityById(Long id) {
        return departmentRepository.findById(id);
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
}
