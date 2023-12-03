package com.zhuk.hospital.controller;

import com.zhuk.hospital.dto.DepartmentDto;
import com.zhuk.hospital.dto.NewDepartmentDto;
import com.zhuk.hospital.dto.UserDepartmentAssociationDTO;
import com.zhuk.hospital.service.DepartmentService;
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
@RequestMapping("/api/hospital/departments")
public class DepartmentController {
    private final DepartmentService departmentService;
    @GetMapping
    @Operation(summary = "Get all departments")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All info is shown"),
            @ApiResponse(responseCode = "401", description = "User is not authorized")
    })
    public ResponseEntity<List<DepartmentDto>> findAll() {
        return ResponseEntity.ok(departmentService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get department by provided id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Department is found"),
            @ApiResponse(responseCode = "401", description = "User is not authorized"),
            @ApiResponse(responseCode = "404", description = "Department with this name doesn't exists")
    })
    public ResponseEntity<DepartmentDto> findById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(departmentService.findById(id));
    }

    @PatchMapping("/add-user")
    @Operation(summary = "Add user to department by provided username and department id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User is added to department"),
            @ApiResponse(responseCode = "401", description = "User is not authorized"),
            @ApiResponse(responseCode = "403", description = "Only admin can add user to department"),
            @ApiResponse(responseCode = "404", description = "User or department doesn't exist")
    })
    public ResponseEntity<?> addUserToDepartment(@RequestBody UserDepartmentAssociationDTO dto) {
        departmentService.addUserToDepartment(dto);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/delete-user")
    @Operation(summary = "Delete user from department by provided username and department id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User is deleted from department"),
            @ApiResponse(responseCode = "401", description = "User is not authorized"),
            @ApiResponse(responseCode = "403", description = "Only admin can delete user from department"),
            @ApiResponse(responseCode = "404", description = "User or department doesn't exist")
    })
    public ResponseEntity<?> deleteUserFromDepartment(@RequestBody UserDepartmentAssociationDTO dto) {
        departmentService.deleteUserFromDepartment(dto);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    @Operation(summary = "Save departments with provided dto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "New department is successfully created"),
            @ApiResponse(responseCode = "400", description = "Department with same name already exists"),
            @ApiResponse(responseCode = "401", description = "User is not authorized"),
            @ApiResponse(responseCode = "403", description = "Only admin can add new users to database")
    })
    public ResponseEntity<DepartmentDto> saveDepartment(@RequestBody NewDepartmentDto newDepartmentDto,
                                            UriComponentsBuilder uriComponentsBuilder) {
        DepartmentDto departmentDto = departmentService.save(newDepartmentDto);
        return ResponseEntity.created(uriComponentsBuilder
                        .path("/api/hospital/departments/{id}")
                        .build(Map.of("id", departmentDto.getId())))
                .body(departmentDto);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update department with provided id and new dto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Department is successfully updated"),
            @ApiResponse(responseCode = "401", description = "User is not authorized"),
            @ApiResponse(responseCode = "403", description = "Only admin can update departments in database")
    })
    public ResponseEntity<DepartmentDto> updateDepartment(@PathVariable("id") Long id, @RequestBody NewDepartmentDto dto,
                                              UriComponentsBuilder uriComponentsBuilder) {
        return ResponseEntity.created(uriComponentsBuilder
                        .path("/api/hospital/departments/{id}")
                        .build(Map.of("id", id)))
                .body(departmentService.updateDepartment(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete department with provided id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Department is successfully deleted"),
            @ApiResponse(responseCode = "401", description = "User is not authorized"),
            @ApiResponse(responseCode = "403", description = "Only admin can delete departments from database")
    })
    public ResponseEntity<?> deleteDepartment(@PathVariable("id") Long id) {
        departmentService.deleteDepartment(id);
        return ResponseEntity.noContent().build();
    }
}
