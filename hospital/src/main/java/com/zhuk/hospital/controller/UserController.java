package com.zhuk.hospital.controller;

import com.zhuk.hospital.dto.CredentialsDto;
import com.zhuk.hospital.dto.UserDto;
import com.zhuk.hospital.service.UserService;
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
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    @GetMapping
    @Operation(summary = "Show all users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All info is shown"),
            @ApiResponse(responseCode = "401", description = "User is not authorized")
    })
    public ResponseEntity<List<UserDto>> findAll() {
        return ResponseEntity.ok(userService.findAll());
    }

    @GetMapping("/{username}")
    @Operation(summary = "Get user by provided username")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User is found"),
            @ApiResponse(responseCode = "401", description = "User is not authorized"),
            @ApiResponse(responseCode = "404", description = "User with this username doesn't exists")
    })
    public ResponseEntity<UserDto> findUserByUsername(@PathVariable("username") String username) {
        return ResponseEntity.ok(userService.findUserByUsername(username));
    }

    @PostMapping
    @Operation(summary = "Save user with provided credentials")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "New user is successfully created"),
            @ApiResponse(responseCode = "400", description = "User with same username already exists"),
            @ApiResponse(responseCode = "401", description = "User is not authorized"),
            @ApiResponse(responseCode = "403", description = "Only admin can add new users to database")
    })
    public ResponseEntity<UserDto> saveUser(@RequestBody CredentialsDto credentialsDto,
                                            UriComponentsBuilder uriComponentsBuilder) {
        return ResponseEntity.created(uriComponentsBuilder
                .path("/api/users/{username}")
                .build(Map.of("username", credentialsDto.getUsername())))
                .body(userService.saveUser(credentialsDto));
    }

    @DeleteMapping("/{username}")
    @Operation(summary = "Delete user with provided username")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User is successfully deleted"),
            @ApiResponse(responseCode = "401", description = "User is not authorized"),
            @ApiResponse(responseCode = "403", description = "Only admin can delete users from database")
    })
    public ResponseEntity<?> deleteUser(@PathVariable("username") String username) {
        userService.deleteUserByUsername(username);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{username}")
    @Operation(summary = "Update user with provided username and new credentials")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User is successfully updated"),
            @ApiResponse(responseCode = "401", description = "User is not authorized"),
            @ApiResponse(responseCode = "403", description = "Only admin can update users in database")
    })
    public ResponseEntity<UserDto> updateUser(@PathVariable("username") String username,
                                              @RequestBody CredentialsDto credentialsDto,
                                              UriComponentsBuilder uriComponentsBuilder) {
        return ResponseEntity.created(uriComponentsBuilder
                        .path("/api/users/{username}")
                        .build(Map.of("username", credentialsDto.getUsername())))
                .body(userService.updateUser(username, credentialsDto));
    }
}
