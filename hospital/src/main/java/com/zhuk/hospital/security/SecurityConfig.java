package com.zhuk.hospital.security;

import com.zhuk.hospital.enums.UserRoleEnum;
import com.zhuk.hospital.repository.UserRepository;
import com.zhuk.hospital.utils.ErrorCodeHelper;
import com.zhuk.hospital.utils.MessageSourceWrapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        String[] allAuthorities = Arrays.stream(UserRoleEnum.values()).map(UserRoleEnum::name).toArray(String[]::new);
        String[] adminAndDoctorAuthorities = {UserRoleEnum.ROLE_ADMIN.name(), UserRoleEnum.ROLE_DOCTOR.name()};
        http
                .authorizeHttpRequests(
                        (authorize) -> authorize
                                .requestMatchers(HttpMethod.GET, "/api/hospital/**")
                                .hasAnyAuthority(allAuthorities)
                                .requestMatchers(HttpMethod.POST,"/api/hospital/tasks")
                                .hasAnyAuthority(adminAndDoctorAuthorities)
                                .requestMatchers(HttpMethod.DELETE,"/api/hospital/tasks/**")
                                .hasAnyAuthority(adminAndDoctorAuthorities)
                                .requestMatchers("/api/**")
                                .hasAuthority(UserRoleEnum.ROLE_ADMIN.name())
                                .anyRequest().authenticated())
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(Customizer.withDefaults())
                .formLogin(Customizer.withDefaults());
        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService,
                                                         PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    UserDetailsService userDetailsService(UserRepository userRepository,
                                          MessageSourceWrapper messageSourceWrapper,
                                          ErrorCodeHelper errorCodeHelper) {
        return new CustomUserDetailsService(userRepository, messageSourceWrapper, errorCodeHelper);
    }
}
