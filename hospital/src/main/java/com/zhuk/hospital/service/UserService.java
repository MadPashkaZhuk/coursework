package com.zhuk.hospital.service;

import com.zhuk.hospital.dto.CredentialsDto;
import com.zhuk.hospital.dto.UserDto;
import com.zhuk.hospital.entity.DepartmentEntity;
import com.zhuk.hospital.entity.UserEntity;
import com.zhuk.hospital.enums.ApiMessageEnum;
import com.zhuk.hospital.enums.ErrorCodeEnum;
import com.zhuk.hospital.enums.UserRoleEnum;
import com.zhuk.hospital.exception.user.UserAlreadyExistsException;
import com.zhuk.hospital.exception.user.UserNotFoundException;
import com.zhuk.hospital.mapper.UserMapper;
import com.zhuk.hospital.repository.UserRepository;
import com.zhuk.hospital.utils.ErrorCodeHelper;
import com.zhuk.hospital.utils.MessageSourceWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final MessageSourceWrapper messageSourceWrapper;
    private final ErrorCodeHelper errorCodeHelper;

    public List<UserDto> findAll() {
        List<UserEntity> list = userRepository.findAll();
        return list.stream()
                .map(userMapper::map)
                .toList();
    }

    public UserDto findUserByUsername(String username) {
        return userMapper.map(getUserEntityByUsernameOrThrowException(username));
    }

    public UserDto saveUser(CredentialsDto credentials) {
        if(getOptionalEntityByUsername(credentials.getUsername()).isPresent()) {
            throw new UserAlreadyExistsException(HttpStatus.BAD_REQUEST,
                    messageSourceWrapper.getMessageCode(ApiMessageEnum.USER_ALREADY_EXISTS),
                    errorCodeHelper.getCode(ErrorCodeEnum.USER_ALREADY_EXISTS_CODE));
        }
        UserEntity user = userRepository.save(
                UserEntity.builder()
                        .username(credentials.getUsername())
                        .password(passwordEncoder.encode(String.valueOf(credentials.getPassword())))
                        .role(UserRoleEnum.valueOf(credentials.getRole()))
                        .build()
        );
        return userMapper.map(user);
    }

    @Transactional
    public void addDepartmentByUsername(String username, DepartmentEntity departmentEntity) {
        UserEntity userEntity = getUserEntityByUsernameOrThrowException(username);
        userEntity.getDepartments().add(departmentEntity);
        userRepository.save(userEntity);
    }

    @Transactional
    public void deleteDepartmentByUsername(String username, DepartmentEntity departmentEntity) {
        UserEntity userEntity = getUserEntityByUsernameOrThrowException(username);
        userEntity.getDepartments().remove(departmentEntity);
        userRepository.save(userEntity);
    }

    @Transactional
    public void deleteUserByUsername(String username) {
        userRepository.deleteByUsername(username);
    }

    @Transactional
    public UserDto updateCredentials(String username, CredentialsDto credentialsDto) {
        Optional<UserEntity> user = getOptionalEntityByUsername(username);
        if(user.isEmpty()) {
            return saveUser(credentialsDto);
        }
        UserEntity entity = user.get();
        userRepository.updateByUsername(entity.getUsername(), credentialsDto.getUsername(),
                passwordEncoder.encode(String.valueOf(credentialsDto.getPassword())));
        return userMapper.map(entity.toBuilder()
                .username(credentialsDto.getUsername())
                .password(String.valueOf(credentialsDto.getPassword()))
                .build());
    }

    public UserEntity getUserEntityByUsernameOrThrowException(String username) {
        return getOptionalEntityByUsername(username).orElseThrow(
                () -> new UserNotFoundException(
                        HttpStatus.NOT_FOUND,
                        messageSourceWrapper.getMessageCode(ApiMessageEnum.USER_NOT_FOUND),
                        errorCodeHelper.getCode(ErrorCodeEnum.USER_NOT_FOUND_CODE)
                )
        );
    }

    private Optional<UserEntity> getOptionalEntityByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}
