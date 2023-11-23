package com.zhuk.coursework.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhuk.coursework.dto.CredentialsDto;
import com.zhuk.coursework.dto.UserDto;
import com.zhuk.coursework.enums.UserRoleEnum;
import com.zhuk.coursework.exception.user.UserAlreadyExistsException;
import com.zhuk.coursework.exception.user.UserNotFoundException;
import com.zhuk.coursework.service.UserService;
import com.zhuk.coursework.utils.ErrorCodeHelper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(UserController.class)
public class UserControllerTest {
    @Autowired
    MockMvc mockMvc;
    @MockBean
    UserService userService;
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    ErrorCodeHelper errorCodeHelper;

    @Test
    public void findAll_ShouldReturnEmptyList_WhenNoData() throws Exception {
        when(userService.findAll()).thenReturn(List.of());
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void findAll_ShouldReturnMedicationList_WhenDataExists() throws Exception {
        UserDto first = UserDto.builder()
                .id(UUID.randomUUID())
                .username("FIRST")
                .password("PASS")
                .role(UserRoleEnum.ROLE_USER)
                .build();
        UserDto second = UserDto.builder()
                .id(UUID.randomUUID())
                .username("SECOND")
                .password("PASS")
                .role(UserRoleEnum.ROLE_USER)
                .build();

        when(userService.findAll()).thenReturn(List.of(first, second));
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(first, second))));
    }

    @Test
    public void findUserByUsername_ShouldReturnDto_WhenUserExists() throws Exception {
        UserDto dto = UserDto.builder()
                .id(UUID.randomUUID())
                .username("FIRST")
                .password("PASS")
                .role(UserRoleEnum.ROLE_USER)
                .build();
        when(userService.findUserByUsername("FIRST")).thenReturn(dto);
        mockMvc.perform(get("/api/users/FIRST"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dto)));
    }

    @Test
    public void findUserByUsername_ShouldReturnNotFound_WhenUserDoesntExist() throws Exception {
        when(userService.findUserByUsername("FIRST"))
                .thenThrow(new UserNotFoundException(HttpStatus.NOT_FOUND, "NOT FOUND", 0));
        mockMvc.perform(get("/api/users/FIRST"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void saveUser_ShouldReturnDto_WhenUserDoesntExist() throws Exception {
        UserDto dto = UserDto.builder()
                .id(UUID.randomUUID())
                .username("USER")
                .password("PASS")
                .role(UserRoleEnum.ROLE_USER)
                .build();
        CredentialsDto credentials = CredentialsDto.builder()
                .username("FIRST").password("PASS".toCharArray()).build();
        when(userService.saveUser(credentials)).thenReturn(dto);
        mockMvc.perform(post("/api/users").
                        contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(credentials)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(dto)));
    }

    @Test
    public void saveUser_ShouldReturnBadRequest_WhenUserAlreadyExists() throws Exception {
        CredentialsDto credentials = CredentialsDto.builder()
                        .username("FIRST").password("PASS".toCharArray()).build();
        when(userService.saveUser(credentials))
                .thenThrow(new UserAlreadyExistsException(HttpStatus.BAD_REQUEST, "BAD REQUEST", 0));
        mockMvc.perform(post("/api/users").
                        contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(credentials)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void deleteUser_ShouldReturnNoContent_WhenHappyPath() throws Exception {
        doNothing().when(userService).deleteUserByUsername("USER");
        mockMvc.perform(delete("/api/users/USER"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void updateUser_ShouldReturnUserDto_WhenHappyPath() throws Exception {
        UserDto dto = UserDto.builder()
                .id(UUID.randomUUID())
                .username("USER")
                .password("PASS")
                .role(UserRoleEnum.ROLE_USER)
                .build();
        CredentialsDto credentials = CredentialsDto.builder()
                .username("FIRST").password("PASS".toCharArray()).build();
        when(userService.updateUser("USER", credentials)).thenReturn(dto);
        mockMvc.perform(put("/api/users/USER")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(credentials)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(dto)));
    }
}
