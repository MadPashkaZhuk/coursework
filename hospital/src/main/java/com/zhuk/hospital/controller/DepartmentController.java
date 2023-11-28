package com.zhuk.hospital.controller;

import com.zhuk.hospital.dto.DepartmentDto;
import com.zhuk.hospital.dto.NewDepartmentDto;
import com.zhuk.hospital.dto.UserDepartmentAssociationDTO;
import com.zhuk.hospital.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/hospital/departments")
public class DepartmentController {
    private final DepartmentService departmentService;
    @GetMapping
    public ResponseEntity<?> findAll() {
        return ResponseEntity.ok(departmentService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(departmentService.findById(id));
    }

    @PatchMapping("/add-user")
    public ResponseEntity<?> addUserToDepartment(@RequestBody UserDepartmentAssociationDTO dto) {
        departmentService.addUserToDepartment(dto);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/delete-user")
    public ResponseEntity<?> deleteUserFromDepartment(@RequestBody UserDepartmentAssociationDTO dto) {
        departmentService.deleteUserFromDepartment(dto);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<?> saveDepartment(@RequestBody NewDepartmentDto newDepartmentDto,
                                            UriComponentsBuilder uriComponentsBuilder) {
        DepartmentDto departmentDto = departmentService.save(newDepartmentDto);
        return ResponseEntity.created(uriComponentsBuilder
                        .path("/api/hospital/departments/{id}")
                        .build(Map.of("id", departmentDto.getId())))
                .body(departmentDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateDepartment(@PathVariable("id") Long id, @RequestBody NewDepartmentDto dto,
                                              UriComponentsBuilder uriComponentsBuilder) {
        return ResponseEntity.created(uriComponentsBuilder
                        .path("/api/hospital/departments/{id}")
                        .build(Map.of("id", id)))
                .body(departmentService.updateDepartment(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDepartment(@PathVariable("id") Long id) {
        departmentService.deleteDepartment(id);
        return ResponseEntity.noContent().build();
    }
}
