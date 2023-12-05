package com.zhuk.medication.security;

import com.zhuk.medication.enums.UserRoleEnum;
import com.zhuk.medication.repository.UserRepository;
import com.zhuk.medication.utils.ErrorCodeHelper;
import com.zhuk.medication.utils.MessageSourceWrapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http
                .authorizeHttpRequests(
                        (authorize) -> authorize
                                .requestMatchers("/api/medication/**")
                                .hasAnyAuthority(UserRoleEnum.ROLE_USER.name(), UserRoleEnum.ROLE_ADMIN.name())
                                .requestMatchers("/api/**").hasAuthority(UserRoleEnum.ROLE_ADMIN.name())
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
