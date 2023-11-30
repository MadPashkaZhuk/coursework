package com.zhuk.hospital.controller;

import com.zhuk.hospital.dto.DepartmentDto;
import com.zhuk.hospital.dto.NewTaskDto;
import com.zhuk.hospital.dto.TaskDto;
import com.zhuk.hospital.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/hospital/tasks")
public class TasksController {
    private final TaskService taskService;

    @GetMapping
    public ResponseEntity<List<TaskDto>> findAll() {
        return ResponseEntity.ok(taskService.findAll());
    }

    @PostMapping
    public ResponseEntity<TaskDto> save(@RequestBody NewTaskDto dto, UriComponentsBuilder uriComponentsBuilder) {
        TaskDto taskDto = taskService.save(dto);
        return ResponseEntity.created(uriComponentsBuilder
                        .path("/api/hospital/tasks/{id}")
                        .build(Map.of("id", taskDto.getId())))
                .body(taskDto);
    }
}
