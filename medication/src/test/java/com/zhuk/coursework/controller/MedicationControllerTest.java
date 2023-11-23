package com.zhuk.coursework.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhuk.coursework.dto.MedicationDto;
import com.zhuk.coursework.dto.NewMedicationDto;
import com.zhuk.coursework.enums.MedicationTypeEnum;
import com.zhuk.coursework.exception.medication.MedicationAlreadyExistsException;
import com.zhuk.coursework.exception.medication.MedicationNotFoundException;
import com.zhuk.coursework.service.MedicationService;
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

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = MedicationController.class)
@AutoConfigureMockMvc(addFilters = false)
public class MedicationControllerTest {
    @Autowired
    MockMvc mockMvc;
    @MockBean
    MedicationService medicationService;
    @MockBean
    ErrorCodeHelper errorCodeHelper;
    @Autowired
    ObjectMapper objectMapper;

    @Test
    public void findAll_ShouldReturnEmptyList_WhenNoData() throws Exception {
        when(medicationService.findAll()).thenReturn(List.of());
        mockMvc.perform(get("/api/medication"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void findAll_ShouldReturnMedicationList_WhenDataExists() throws Exception {
        MedicationDto first = MedicationDto.builder()
                .id(1L)
                .name("FIRST")
                .manufacturer("TEST")
                .type(MedicationTypeEnum.PEN)
                .weight(400)
                .requirePrescription(false)
                .additionalInfo("FIRST INFO")
                .build();
        MedicationDto second = MedicationDto.builder()
                .id(2L)
                .name("SECOND")
                .manufacturer("TEST")
                .type(MedicationTypeEnum.PEN)
                .weight(500)
                .requirePrescription(true)
                .additionalInfo("SECOND INFO")
                .build();

        when(medicationService.findAll()).thenReturn(List.of(first, second));
        mockMvc.perform(get("/api/medication"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(first, second))));
    }

    @Test
    public void findById_ShouldReturnDto_WhenMedicationExists() throws Exception {
        MedicationDto dto = MedicationDto.builder()
                .id(1L)
                .name("FIRST")
                .manufacturer("TEST")
                .type(MedicationTypeEnum.PEN)
                .weight(400)
                .requirePrescription(false)
                .additionalInfo("FIRST INFO")
                .build();
        when(medicationService.findById(1L)).thenReturn(dto);
        mockMvc.perform(get("/api/medication/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(dto)));
    }

    @Test
    public void findById_ShouldReturnNotFound_WhenMedicationDoesntExist() throws Exception {
        when(medicationService.findById(1L)).thenThrow(new MedicationNotFoundException(HttpStatus.NOT_FOUND,
                "NOT FOUND", 0));
        mockMvc.perform(get("/api/medication/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void saveMedication_ShouldReturnDto_WhenMedicationDoesntExist() throws Exception {
        MedicationDto dto = MedicationDto.builder()
                .id(1L)
                .name("FIRST")
                .manufacturer("TEST")
                .type(MedicationTypeEnum.PEN)
                .weight(400)
                .requirePrescription(false)
                .additionalInfo("FIRST INFO")
                .build();
        NewMedicationDto newMedicationDto = NewMedicationDto.builder()
                .name("FIRST")
                .manufacturer("TEST")
                .type("PEN")
                .weight(400)
                .requirePrescription(false)
                .additionalInfo("FIRST INFO")
                .build();
        when(medicationService.saveMedication(newMedicationDto)).thenReturn(dto);
        mockMvc.perform(post("/api/medication").
                 contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newMedicationDto)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(dto)));
    }

    @Test
    public void saveMedication_ShouldReturnBadRequest_WhenMedicationAlreadyExists() throws Exception {
        NewMedicationDto dto = NewMedicationDto.builder()
                .name("FIRST")
                .manufacturer("TEST")
                .type("PEN")
                .weight(400)
                .requirePrescription(false)
                .additionalInfo("FIRST INFO")
                .build();
        when(medicationService.saveMedication(dto))
                .thenThrow(new MedicationAlreadyExistsException(HttpStatus.BAD_REQUEST, "BAD REQUEST", 0));
        mockMvc.perform(post("/api/medication")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void deleteMedication_ShouldReturnNoContent_WhenHappyPath() throws Exception {
        doNothing().when(medicationService).deleteMedication(1L);
        mockMvc.perform(delete("/api/medication/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void updateMedication_ShouldSaveMedication_WhenHappyPath() throws Exception {
        MedicationDto dto = MedicationDto.builder()
                .id(1L)
                .name("FIRST")
                .manufacturer("TEST")
                .type(MedicationTypeEnum.PEN)
                .weight(400)
                .requirePrescription(false)
                .additionalInfo("FIRST INFO")
                .build();
        NewMedicationDto newMedicationDto = NewMedicationDto.builder()
                .name("FIRST")
                .manufacturer("TEST")
                .type("PEN")
                .weight(400)
                .requirePrescription(false)
                .additionalInfo("FIRST INFO")
                .build();
        when(medicationService.updateMedication(newMedicationDto))
                .thenReturn(dto);
        mockMvc.perform(put("/api/medication")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newMedicationDto)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(dto)));
    }
}
