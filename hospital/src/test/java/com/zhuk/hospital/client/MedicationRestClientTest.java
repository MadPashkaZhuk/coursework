package com.zhuk.hospital.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhuk.hospital.dto.UpdateMedicationQuantityDto;
import com.zhuk.hospital.exception.medication.MedicationBadRequestException;
import com.zhuk.hospital.exception.medication.MedicationForbiddenException;
import com.zhuk.hospital.exception.medication.MedicationNotFoundException;
import com.zhuk.hospital.exception.medication.MedicationUnauthorizedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@SpringBootTest
public class MedicationRestClientTest {
    @Autowired
    RestTemplate restTemplate;
    @Autowired
    private MedicationRestClient medicationRestClient;
    @Autowired
    MedicationApiProperties apiProperties;
    private MockRestServiceServer mockServer;
    @Autowired
    ObjectMapper objectMapper;

    @BeforeEach
    public void init() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }
    @Test
    public void reduceQuantity_ShouldDoNothing_WhenHappyPath() throws Exception{
        UpdateMedicationQuantityDto dto = UpdateMedicationQuantityDto.builder()
                .quantity(-4)
                .build();
        mockServer.expect(requestTo(getDefaultUriForMedicationId(1L)))
                .andExpect(method(HttpMethod.PATCH))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(dto)))
                .andRespond(withSuccess());
        medicationRestClient.reduceQuantity(1L, 4);
        mockServer.verify();
    }

    @Test
    public void increaseQuantity_ShouldDoNothing_WhenHappyPath() throws Exception {
        UpdateMedicationQuantityDto dto = UpdateMedicationQuantityDto.builder()
                .quantity(4)
                .build();
        mockServer.expect(requestTo(getDefaultUriForMedicationId(1L)))
                .andExpect(method(HttpMethod.PATCH))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(dto)))
                .andRespond(withSuccess());
        medicationRestClient.increaseQuantity(1L, 4);
        mockServer.verify();
    }

    @Test
    public void reduceQuantity_ShouldThrowMedicationBadRequest_WhenBadRequest() throws Exception {
        UpdateMedicationQuantityDto dto = UpdateMedicationQuantityDto.builder()
                .quantity(4)
                .build();
        mockServer.expect(requestTo(getDefaultUriForMedicationId(1L)))
                .andExpect(method(HttpMethod.PATCH))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(dto)))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST));
        assertThrows(MedicationBadRequestException.class, () -> medicationRestClient.reduceQuantity(1L, -4));
    }

    @Test
    public void reduceQuantity_ShouldThrowMedicationUnauthorized_WhenUnauthorized() throws Exception {
        UpdateMedicationQuantityDto dto = UpdateMedicationQuantityDto.builder()
                .quantity(4)
                .build();
        mockServer.expect(requestTo(getDefaultUriForMedicationId(1L)))
                .andExpect(method(HttpMethod.PATCH))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(dto)))
                .andRespond(withStatus(HttpStatus.UNAUTHORIZED));
        assertThrows(MedicationUnauthorizedException.class, () -> medicationRestClient.reduceQuantity(1L, -4));
    }

    @Test
    public void reduceQuantity_ShouldThrowMedicationForbidden_WhenForbidden() throws Exception {
        UpdateMedicationQuantityDto dto = UpdateMedicationQuantityDto.builder()
                .quantity(4)
                .build();
        mockServer.expect(requestTo(getDefaultUriForMedicationId(1L)))
                .andExpect(method(HttpMethod.PATCH))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(dto)))
                .andRespond(withStatus(HttpStatus.FORBIDDEN));
        assertThrows(MedicationForbiddenException.class, () -> medicationRestClient.reduceQuantity(1L, -4));
    }

    @Test
    public void reduceQuantity_ShouldThrowMedicationNotFound_WhenNotFound() throws Exception {
        UpdateMedicationQuantityDto dto = UpdateMedicationQuantityDto.builder()
                .quantity(4)
                .build();
        mockServer.expect(requestTo(getDefaultUriForMedicationId(1L)))
                .andExpect(method(HttpMethod.PATCH))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(dto)))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));
        assertThrows(MedicationNotFoundException.class, () -> medicationRestClient.reduceQuantity(1L, -4));
    }

    private String getDefaultUriForMedicationId(Long medicationId) {
        return UriComponentsBuilder.fromUriString(apiProperties.getUrl() + "/"+ medicationId.toString())
                .toUriString();
    }
}
