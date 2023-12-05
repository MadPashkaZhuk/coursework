package com.zhuk.medication.controller;

import com.zhuk.medication.dto.MedicationDto;
import com.zhuk.medication.dto.NewMedicationDto;
import com.zhuk.medication.dto.UpdateQuantityDto;
import com.zhuk.medication.service.MedicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/medication")
public class MedicationController {
    private final MedicationService medicationService;

    @Operation(summary = "Show all medication")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All information is shown."),
            @ApiResponse(responseCode = "401", description = "User is not authorized.")
    })
    @GetMapping
    public ResponseEntity<List<MedicationDto>> findAll() {
        return ResponseEntity.ok(medicationService.findAll());
    }

    @Operation(summary = "Show medication by provided id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Medication requested is shown."),
            @ApiResponse(responseCode = "401", description = "User is not authorized."),
            @ApiResponse(responseCode = "403", description = "Error code 7004: That medication is connected to another user."),
            @ApiResponse(responseCode = "404", description = "Error code 7001: Medication with this is doesn't exists")
    })
    @GetMapping("/{id}")
    public ResponseEntity<MedicationDto> findById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(medicationService.findById(id));
    }
    @Operation(summary = "Save medication")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Medication provided is successfully saved."),
            @ApiResponse(responseCode = "400", description = "Error code 7002: Medication with the same name and weight already exists."),
            @ApiResponse(responseCode = "401", description = "User is not authorized."),
            @ApiResponse(responseCode = "403", description = "Only admin can add medication to database.")
    })
    @PostMapping
    public ResponseEntity<MedicationDto> saveMedication(@RequestBody NewMedicationDto dto,
                                                        UriComponentsBuilder uriComponentsBuilder) {
        MedicationDto response = medicationService.saveMedication(dto);
        return ResponseEntity.created(uriComponentsBuilder.path("/api/medication/{id}")
                        .build(Map.of("id", response.getId())))
                .body(response);

    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete medication by provided id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Medication is deleted."),
            @ApiResponse(responseCode = "401", description = "User is not authorized."),
            @ApiResponse(responseCode = "403", description = "Only admin can delete medication from database.<br>" +
                    "Error code 7004: That medication is connected to another user")
    })
    public ResponseEntity<?> deleteMedication(@PathVariable("id") Long id) {
        medicationService.deleteMedication(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Update medication by provided id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Medication provided is successfully updated."),
            @ApiResponse(responseCode = "401", description = "User is not authorized."),
            @ApiResponse(responseCode = "403", description = "Only admin can update medication in database.<br>" +
                    "Error code 7004: That medication is connected to another user."),
            @ApiResponse(responseCode = "404", description = "Error code 7001: Medication with this is doesn't exists")
    })
    @PutMapping
    public ResponseEntity<MedicationDto> updateMedication(@RequestBody NewMedicationDto dto,
                                                          UriComponentsBuilder uriComponentsBuilder) {
        MedicationDto response = medicationService.updateMedication(dto);
        return ResponseEntity.created(uriComponentsBuilder.path("/api/medication/{id}")
                        .build(Map.of("id", response.getId())))
                .body(response);
    }
    @Operation(summary = "Update quantity of medication by provided id: positive for increasing and negative for decreasing")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Medication provided is successfully updated."),
            @ApiResponse(responseCode = "401", description = "User is not authorized."),
            @ApiResponse(responseCode = "403", description = "Only admin can update medication in database.<br>" +
                    "Error code 7004: That medication is connected to another user."),
            @ApiResponse(responseCode = "404", description = "Error code 7001: Medication with this is doesn't exists")
    })
    @PatchMapping("/{id}")
    public ResponseEntity<?> updateQuantityForMedication(@PathVariable("id") Long id, @RequestBody UpdateQuantityDto dto) {
        medicationService.updateQuantity(id, dto);
        return ResponseEntity.ok().build();
    }
}
