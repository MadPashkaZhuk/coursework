package com.zhuk.hospital.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhuk.hospital.dto.DepartmentDto;
import com.zhuk.hospital.dto.NewDepartmentDto;
import com.zhuk.hospital.dto.UserDepartmentAssociationDTO;
import com.zhuk.hospital.exception.department.DepartmentAlreadyExistsException;
import com.zhuk.hospital.exception.department.DepartmentNotFoundException;
import com.zhuk.hospital.service.DepartmentService;
import com.zhuk.hospital.utils.ErrorCodeHelper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(DepartmentController.class)
public class DepartmentControllerTest {
    @Autowired
    MockMvc mockMvc;
    @MockBean
    DepartmentService departmentService;
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    ErrorCodeHelper errorCodeHelper;
    @Test
    public void findAll_ShouldReturnEmptyList_WhenNoData() throws Exception {
        when(departmentService.findAll()).thenReturn(List.of());
        mockMvc.perform(get("/api/hospital/departments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void findAll_ShouldReturnDepartmentList_WhenDataExists() throws Exception {
        DepartmentDto first = DepartmentDto.builder()
                .id(1L)
                .name("FIRST")
                .description("FIRST DESCR-N")
                .tasks(new ArrayList<>())
                .build();
        DepartmentDto second = DepartmentDto.builder()
                .id(2L)
                .name("SECOND")
                .description("SECOND DESCR-N")
                .tasks(new ArrayList<>())
                .build();
        when(departmentService.findAll()).thenReturn(List.of(first, second));
        mockMvc.perform(get("/api/hospital/departments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(first, second))));
    }

    @Test
    public void findById_ShouldReturnDto_WhenDepartmentExists() throws Exception {
        DepartmentDto dto = DepartmentDto.builder()
                .id(1L)
                .name("FIRST")
                .description("FIRST DESCR-N")
                .tasks(new ArrayList<>())
                .build();
        when(departmentService.findById(1L)).thenReturn(dto);
        mockMvc.perform(get("/api/hospital/departments/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dto)));
    }

    @Test
    public void findById_ShouldReturnNotFound_WhenDepartmentDoesntExist() throws Exception {
        when(departmentService.findById(999L))
                .thenThrow(new DepartmentNotFoundException(HttpStatus.NOT_FOUND, "NOT FOUND", 0));
        mockMvc.perform(get("/api/hospital/departments/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void saveDepartment_ShouldReturnDto_WhenDepartmentDoesntExist() throws Exception {
        DepartmentDto dto = DepartmentDto.builder()
                .id(1L)
                .name("FIRST")
                .description("FIRST DESCR-N")
                .tasks(new ArrayList<>())
                .build();
        NewDepartmentDto newDepartmentDto = NewDepartmentDto.builder()
                .name("FIRST")
                .description("FIRST DESCR-N")
                .build();
        when(departmentService.save(newDepartmentDto)).thenReturn(dto);
        mockMvc.perform(post("/api/hospital/departments").
                        contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newDepartmentDto)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(dto)));
    }

    @Test
    public void saveDepartment_ShouldReturnBadRequest_WhenDepartmentAlreadyExists() throws Exception {
        NewDepartmentDto newDepartmentDto = NewDepartmentDto.builder()
                .name("FIRST")
                .description("FIRST DESCR-N")
                .build();
        when(departmentService.save(newDepartmentDto))
                .thenThrow(new DepartmentAlreadyExistsException(HttpStatus.BAD_REQUEST, "BAD REQUEST", 0));
        mockMvc.perform(post("/api/hospital/departments").
                        contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newDepartmentDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void deleteDepartment_ShouldReturnNoContent_WhenHappyPath() throws Exception {
        doNothing().when(departmentService).deleteDepartment(1L);
        mockMvc.perform(delete("/api/hospital/departments/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void updateDepartment_ShouldReturnDepartmentDto_WhenHappyPath() throws Exception {
        DepartmentDto dto = DepartmentDto.builder()
                .id(1L)
                .name("FIRST")
                .description("FIRST DESCR-N")
                .tasks(new ArrayList<>())
                .build();
        NewDepartmentDto newDepartmentDto = NewDepartmentDto.builder()
                .name("FIRST")
                .description("FIRST DESCR-N")
                .build();
        when(departmentService.updateDepartment(1L, newDepartmentDto)).thenReturn(dto);
        mockMvc.perform(put("/api/hospital/departments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newDepartmentDto)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(dto)));
    }

    @Test
    public void updateDepartment_ShouldReturnBadRequest_WhenDepartmentWithSameNameExists() throws Exception {
        NewDepartmentDto newDepartmentDto = NewDepartmentDto.builder()
                .name("FIRST")
                .description("FIRST DESCR-N")
                .build();
        when(departmentService.updateDepartment(1L, newDepartmentDto))
                .thenThrow(new DepartmentAlreadyExistsException(HttpStatus.BAD_REQUEST, "BAD REQUEST", 0));
        mockMvc.perform(put("/api/hospital/departments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newDepartmentDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void addUserToDepartment_ShouldReturnNoContent_WhenHappyPath() throws Exception {
        UserDepartmentAssociationDTO dto = UserDepartmentAssociationDTO.builder()
                .departmentId(1L)
                .username("USER")
                .build();
        doNothing().when(departmentService).addUserToDepartment(dto);
        mockMvc.perform(patch("/api/hospital/departments/add-user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNoContent());
    }

    @Test
    public void deleteUserFromDepartment_ShouldReturnNoContent_WhenHappyPath() throws Exception {
        UserDepartmentAssociationDTO dto = UserDepartmentAssociationDTO.builder()
                .departmentId(1L)
                .username("USER")
                .build();
        doNothing().when(departmentService).deleteUserFromDepartment(dto);
        mockMvc.perform(patch("/api/hospital/departments/delete-user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNoContent());
    }
}
