package com.zhuk.coursework.security;

import com.zhuk.coursework.entity.UserEntity;
import com.zhuk.coursework.enums.ApiMessageEnum;
import com.zhuk.coursework.enums.ErrorCodeEnum;
import com.zhuk.coursework.exception.user.UserNotFoundException;
import com.zhuk.coursework.repository.UserRepository;
import com.zhuk.coursework.utils.ErrorCodeHelper;
import com.zhuk.coursework.utils.MessageSourceWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;
    private final MessageSourceWrapper messageSourceWrapper;
    private final ErrorCodeHelper errorCodeHelper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserEntity> userEntity = userRepository.findByUsername(username);
        return userEntity.map(CustomUserDetails::new)
                .orElseThrow(() -> new UserNotFoundException(
                        HttpStatus.NOT_FOUND,
                        messageSourceWrapper.getMessageCode(ApiMessageEnum.USER_NOT_FOUND),
                        errorCodeHelper.getCode(ErrorCodeEnum.USER_NOT_FOUND_CODE)
                ));
    }
}
