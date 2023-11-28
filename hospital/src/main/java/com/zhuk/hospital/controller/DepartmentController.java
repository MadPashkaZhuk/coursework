package com.zhuk.hospital.controller;

import com.zhuk.hospital.dto.UserDepartmentAssociationDTO;
import com.zhuk.hospital.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/hospital/departments")
public class DepartmentController {
    private final DepartmentService departmentService;
    @GetMapping
    public ResponseEntity<?> findAll() {
        return ResponseEntity.ok(departmentService.findAll());
    }

    @PatchMapping
    public ResponseEntity<?> addUserToDepartment(@RequestBody UserDepartmentAssociationDTO dto) {
        departmentService.addUserToDepartment(dto);
        return ResponseEntity.noContent().build();
    }
}
