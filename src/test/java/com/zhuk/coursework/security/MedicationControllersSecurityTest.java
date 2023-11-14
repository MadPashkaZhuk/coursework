package com.zhuk.coursework.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhuk.coursework.dto.MedicationDto;
import com.zhuk.coursework.dto.NewMedicationDto;
import com.zhuk.coursework.enums.MedicationTypeEnum;
import com.zhuk.coursework.service.MedicationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class MedicationControllersSecurityTest {
    @Autowired
    MockMvc mockMvc;
    @MockBean
    MedicationService medicationService;
    @Autowired
    ObjectMapper objectMapper;

    @Test
    @WithAnonymousUser
    public void findAll_ShouldReturnUnauthorized_WhenAnonymous() throws Exception {
        mockMvc.perform(get("/api/medication"))
                .andExpect(status().isUnauthorized())
                .andReturn();
    }

    @Test
    @WithMockUser
    public void findAll_ShouldReturnOkStatus_WhenAuthorized() throws Exception {
        mockMvc.perform(get("/api/medication"))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    @WithAnonymousUser
    public void findById_ShouldReturnUnauthorized_WhenAnonymous() throws Exception {
        mockMvc.perform(get("/api/medication/1"))
                .andExpect(status().isUnauthorized())
                .andReturn();
    }

    @Test
    @WithMockUser
    public void findById_ShouldReturnOkStatus_WhenAnonymous() throws Exception {
        mockMvc.perform(get("/api/medication/1"))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    @WithAnonymousUser
    public void saveMedication_ShouldReturnUnauthorized_WhenAnonymous() throws Exception {
        NewMedicationDto dto = NewMedicationDto.builder()
                .name("FIRST")
                .manufacturer("TEST")
                .type("PEN")
                .weight(400)
                .requirePrescription(false)
                .additionalInfo("FIRST INFO")
                .build();
        mockMvc.perform(post("/api/medication")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized())
                .andReturn();
    }

    @Test
    @WithMockUser
    public void saveMedication_ShouldReturnForbidden_WhenUser() throws Exception {
        NewMedicationDto dto = NewMedicationDto.builder()
                .name("FIRST")
                .manufacturer("TEST")
                .type("PEN")
                .weight(400)
                .requirePrescription(false)
                .additionalInfo("FIRST INFO")
                .build();
        mockMvc.perform(post("/api/medication")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void saveMedication_ShouldReturnCreated_WhenAdmin() throws Exception {
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
        mockMvc.perform(post("/api/medication")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn();
    }
    @Test
    @WithAnonymousUser
    public void deleteMedication_ShouldReturnUnauthorized_WhenAnonymous() throws Exception {
        mockMvc.perform(delete("/api/medication/1"))
                .andExpect(status().isUnauthorized())
                .andReturn();
    }

    @Test
    @WithMockUser
    public void deleteMedication_ShouldReturnForbidden_WhenUser() throws Exception {
        mockMvc.perform(delete("/api/medication/1"))
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void deleteMedication_ShouldReturnNoContent_WhenAdmin() throws Exception {
        doNothing().when(medicationService).deleteMedication(1L);
        mockMvc.perform(delete("/api/medication/1"))
                .andExpect(status().isNoContent())
                .andReturn();
    }

    @Test
    @WithAnonymousUser
    public void updateMedication_ShouldReturnUnauthorized_WhenAnonymous() throws Exception {
        NewMedicationDto newMedicationDto = NewMedicationDto.builder()
                .name("FIRST")
                .manufacturer("TEST")
                .type("PEN")
                .weight(400)
                .requirePrescription(false)
                .additionalInfo("FIRST INFO")
                .build();
        mockMvc.perform(put("/api/medication")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newMedicationDto)))
                .andExpect(status().isUnauthorized())
                .andReturn();
    }

    @Test
    @WithMockUser
    public void updateMedication_ShouldReturnForbidden_WhenUser() throws Exception {
        NewMedicationDto newMedicationDto = NewMedicationDto.builder()
                .name("FIRST")
                .manufacturer("TEST")
                .type("PEN")
                .weight(400)
                .requirePrescription(false)
                .additionalInfo("FIRST INFO")
                .build();
        mockMvc.perform(put("/api/medication")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newMedicationDto)))
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void updateMedication_ShouldReturnCreated_WhenAdmin() throws Exception {
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
        when(medicationService.updateMedication(newMedicationDto)).thenReturn(dto);
        mockMvc.perform(put("/api/medication")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newMedicationDto)))
                .andExpect(status().isCreated())
                .andReturn();
    }
}
