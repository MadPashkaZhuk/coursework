package com.zhuk.hospital.controller;

import com.zhuk.hospital.dto.NewTaskDto;
import com.zhuk.hospital.dto.TaskDto;
import com.zhuk.hospital.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/hospital/tasks")
public class TasksController {
    private final TaskService taskService;
    @Operation(summary = "Get all tasks for today for departments connected to current user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All tasks are shown"),
            @ApiResponse(responseCode = "401", description = "User is not authorized")
    })
    @GetMapping
    public ResponseEntity<List<TaskDto>> findAll() {
        return ResponseEntity.ok(taskService.findAll());
    }

    @Operation(summary = "Get task by provided id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All tasks are shown"),
            @ApiResponse(responseCode = "401", description = "User is not authorized"),
            @ApiResponse(responseCode = "404", description = "Task with provided id doesn't exist")
    })
    @GetMapping("/{id}")
    public ResponseEntity<TaskDto> findById(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(taskService.findById(id));
    }

    @Operation(summary = "Add tasks with NewTaskDto, " +
            "amount of added tasks equals to amountOfDays * dateTimeOfIssue.size()")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "All tasks are added"),
            @ApiResponse(responseCode = "401", description = "User is not authorized"),
            @ApiResponse(responseCode = "403", description = "Only admin/doctor can add tasks"),
            @ApiResponse(responseCode = "404", description = "Department provided doesn't exists")
    })
    @PostMapping
    public ResponseEntity<List<TaskDto>> save(@RequestBody NewTaskDto dto, UriComponentsBuilder uriComponentsBuilder) {
        List<TaskDto> taskDtoList = taskService.save(dto);
        return ResponseEntity.created(uriComponentsBuilder.path("/api/hospital/tasks").build().toUri())
                .body(taskDtoList);
    }

    @Operation(summary = "Delete task with provided id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Task is deleted"),
            @ApiResponse(responseCode = "401", description = "User is not authorized"),
            @ApiResponse(responseCode = "403", description = "Only admin/doctor can delete task")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") UUID id) {
        taskService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
