package com.zhuk.coursework.service;

import com.zhuk.coursework.dto.CredentialsDto;
import com.zhuk.coursework.dto.UserDto;
import com.zhuk.coursework.entity.UserEntity;
import com.zhuk.coursework.enums.ApiMessageEnum;
import com.zhuk.coursework.enums.ErrorCodeEnum;
import com.zhuk.coursework.enums.UserRoleEnum;
import com.zhuk.coursework.exception.user.UserAlreadyExistsException;
import com.zhuk.coursework.exception.user.UserNotFoundException;
import com.zhuk.coursework.mapper.UserMapper;
import com.zhuk.coursework.repository.UserRepository;
import com.zhuk.coursework.utils.ErrorCodeHelper;
import com.zhuk.coursework.utils.MessageSourceWrapper;
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
        if(getOptionalEntityByUsername(credentials.getUsername()).isEmpty()) {
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
        UserEntity entity = user.get();
        userRepository.updateByUsername(entity.getUsername(), credentialsDto.getUsername(),
                String.valueOf(credentialsDto.getPassword()));
        return userMapper.map(entity.toBuilder()
                .username(credentialsDto.getUsername())
                .password(String.valueOf(credentialsDto.getPassword()))
                .build());
    }

    private UserEntity getUserEntityByUsernameOrThrowException(String username) {
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
