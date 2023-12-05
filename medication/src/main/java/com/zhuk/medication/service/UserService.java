package com.zhuk.medication.service;

import com.zhuk.medication.dto.CredentialsDto;
import com.zhuk.medication.dto.UserDto;
import com.zhuk.medication.entity.UserEntity;
import com.zhuk.medication.enums.ApiMessageEnum;
import com.zhuk.medication.enums.ErrorCodeEnum;
import com.zhuk.medication.enums.UserRoleEnum;
import com.zhuk.medication.exception.user.UserAlreadyExistsException;
import com.zhuk.medication.exception.user.UserNotFoundException;
import com.zhuk.medication.mapper.UserMapper;
import com.zhuk.medication.repository.UserRepository;
import com.zhuk.medication.utils.ErrorCodeHelper;
import com.zhuk.medication.utils.MessageSourceWrapper;
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
                        .role(UserRoleEnum.ROLE_USER)
                        .build()
        );
        return userMapper.map(user);
    }

    @Transactional
    public void deleteUserByUsername(String username) {
        userRepository.deleteByUsername(username);
    }

    @Transactional
    public UserDto updateUser(String username, CredentialsDto credentialsDto) {
        Optional<UserEntity> user = getOptionalEntityByUsername(username);
        if(user.isEmpty()) {
            return saveUser(credentialsDto);
        }
        if(getOptionalEntityByUsername(credentialsDto.getUsername()).isPresent() &&
                getOptionalEntityByUsername(credentialsDto.getUsername()).get().getId().equals(user.get().getId())) {
            throw new UserAlreadyExistsException(HttpStatus.BAD_REQUEST,
                    messageSourceWrapper.getMessageCode(ApiMessageEnum.USER_ALREADY_EXISTS),
                    errorCodeHelper.getCode(ErrorCodeEnum.USER_ALREADY_EXISTS_CODE));
        }
        userRepository.updateByUsername(user.get().getUsername(), credentialsDto.getUsername(),
                passwordEncoder.encode(String.valueOf(credentialsDto.getPassword())));
        return userMapper.map(user.get().toBuilder()
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
