package com.zhuk.coursework.controller;

import com.zhuk.coursework.dto.CredentialsDto;
import com.zhuk.coursework.dto.UserDto;
import com.zhuk.coursework.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    @GetMapping
    public ResponseEntity<List<UserDto>> findAll() {
        return ResponseEntity.ok(userService.findAll());
    }

    @GetMapping("/{username}")
    public ResponseEntity<UserDto> getUserByUsername(@PathVariable("username") String username) {
        return ResponseEntity.ok(userService.findUserByUsername(username));
    }

    @PostMapping
    public ResponseEntity<UserDto> saveUser(@RequestBody CredentialsDto credentialsDto,
                                            UriComponentsBuilder uriComponentsBuilder) {
        return ResponseEntity.created(uriComponentsBuilder
                .path("/api/users/{username}")
                .build(Map.of("username", credentialsDto.getUsername())))
                .body(userService.saveUser(credentialsDto));
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<?> deleteUser(@PathVariable("username") String username) {
        userService.deleteUserByUsername(username);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{username}")
    public ResponseEntity<UserDto> updateUser(@PathVariable("username") String username,
                                              @RequestBody CredentialsDto credentialsDto,
                                              UriComponentsBuilder uriComponentsBuilder) {
        return ResponseEntity.created(uriComponentsBuilder
                        .path("/api/users/{username}")
                        .build(Map.of("username", credentialsDto.getUsername())))
                .body(userService.updateUser(username, credentialsDto));
    }
}
