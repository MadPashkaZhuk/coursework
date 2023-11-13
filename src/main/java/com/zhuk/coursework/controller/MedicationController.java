package com.zhuk.coursework.controller;

import com.zhuk.coursework.dto.MedicationDto;
import com.zhuk.coursework.dto.NewMedicationDto;
import com.zhuk.coursework.service.MedicationService;
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

    @GetMapping
    public ResponseEntity<List<MedicationDto>> findAll() {
        return ResponseEntity.ok(medicationService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MedicationDto> findById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(medicationService.findById(id));
    }

    @PostMapping
    public ResponseEntity<MedicationDto> saveMedication(@RequestBody NewMedicationDto newMedicationDto,
                                                        UriComponentsBuilder uriComponentsBuilder) {
        MedicationDto dto = medicationService.saveMedication(newMedicationDto);
        return ResponseEntity.created(uriComponentsBuilder.path("/api/medication/{id}")
                        .build(Map.of("id", dto.getId())))
                        .body(dto);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMedication(@PathVariable("id") Long id) {
        medicationService.deleteMedication(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<MedicationDto> updateMedication(@PathVariable("id") Long id,
                                                          @RequestBody NewMedicationDto newMedicationDto,
                                                          UriComponentsBuilder uriComponentsBuilder) {
        return ResponseEntity.created(uriComponentsBuilder.path("/api/medication/{id}")
                        .build(Map.of("id", id)))
                .body(medicationService.updateMedication(newMedicationDto, id));
    }
}
