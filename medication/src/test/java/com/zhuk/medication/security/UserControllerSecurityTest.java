package com.zhuk.medication.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhuk.medication.dto.CredentialsDto;
import com.zhuk.medication.dto.UserDto;
import com.zhuk.medication.enums.UserRoleEnum;
import com.zhuk.medication.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerSecurityTest {
    @Autowired
    MockMvc mockMvc;
    @MockBean
    UserService userService;
    @Autowired
    ObjectMapper objectMapper;

    @Test
    @WithAnonymousUser
    public void findAll_ShouldReturnUnauthorized_WhenAnonymous() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isUnauthorized())
                .andReturn();
    }

    @Test
    @WithMockUser
    public void findAll_ShouldReturnForbiddenStatus_WhenAuthorized() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void findAll_ShouldReturnOkStatus_WhenAdmin() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    @WithAnonymousUser
    public void findByUsername_ShouldReturnUnauthorized_WhenAnonymous() throws Exception {
        mockMvc.perform(get("/api/users/USER"))
                .andExpect(status().isUnauthorized())
                .andReturn();
    }

    @Test
    @WithMockUser
    public void findByUsername_ShouldReturnForbiddenStatus_WhenUser() throws Exception {
        mockMvc.perform(get("/api/users/USER"))
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void findByUsername_ShouldReturnOkStatus_WhenAdmin() throws Exception {
        mockMvc.perform(get("/api/users/USER"))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    @WithAnonymousUser
    public void saveUser_ShouldReturnUnauthorized_WhenAnonymous() throws Exception {
        CredentialsDto credentials = CredentialsDto.builder()
                .username("FIRST").password("PASS".toCharArray()).build();
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(credentials)))
                .andExpect(status().isUnauthorized())
                .andReturn();
    }

    @Test
    @WithMockUser
    public void saveUser_ShouldReturnForbidden_WhenUser() throws Exception {
        CredentialsDto credentials = CredentialsDto.builder()
                .username("FIRST").password("PASS".toCharArray()).build();
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(credentials)))
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void saveUser_ShouldReturnCreated_WhenAdmin() throws Exception {
        UserDto dto = UserDto.builder()
                .id(UUID.randomUUID())
                .username("USER")
                .password("PASS")
                .role(UserRoleEnum.ROLE_USER)
                .build();
        CredentialsDto credentials = CredentialsDto.builder()
                .username("FIRST").password("PASS".toCharArray()).build();
        when(userService.saveUser(credentials)).thenReturn(dto);
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn();
    }
    @Test
    @WithAnonymousUser
    public void deleteUser_ShouldReturnUnauthorized_WhenAnonymous() throws Exception {
        mockMvc.perform(delete("/api/users/USER"))
                .andExpect(status().isUnauthorized())
                .andReturn();
    }

    @Test
    @WithMockUser
    public void deleteUser_ShouldReturnForbidden_WhenUser() throws Exception {
        mockMvc.perform(delete("/api/users/USER"))
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void deleteMedication_ShouldReturnNoContent_WhenAdmin() throws Exception {
        doNothing().when(userService).deleteUserByUsername("USER");
        mockMvc.perform(delete("/api/users/USER"))
                .andExpect(status().isNoContent())
                .andReturn();
    }

    @Test
    @WithAnonymousUser
    public void updateMedication_ShouldReturnUnauthorized_WhenAnonymous() throws Exception {
        CredentialsDto credentials = CredentialsDto.builder()
                .username("FIRST").password("PASS".toCharArray()).build();
        mockMvc.perform(put("/api/users/USER")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(credentials)))
                .andExpect(status().isUnauthorized())
                .andReturn();
    }

    @Test
    @WithMockUser
    public void updateMedication_ShouldReturnForbidden_WhenUser() throws Exception {
        CredentialsDto credentials = CredentialsDto.builder()
                .username("FIRST").password("PASS".toCharArray()).build();
        mockMvc.perform(put("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(credentials)))
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void updateMedication_ShouldReturnCreated_WhenAdmin() throws Exception {
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
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn();
    }
}
