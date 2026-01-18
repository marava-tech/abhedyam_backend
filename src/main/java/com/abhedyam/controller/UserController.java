package com.abhedyam.controller;

import com.abhedyam.dto.ApiResponse;
import com.abhedyam.dto.UserCreateRequest;
import com.abhedyam.dto.UserResponse;
import com.abhedyam.dto.UserUpdateRequest;
import com.abhedyam.service.interfaces.IUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    
    private final IUserService userService;
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<UserResponse> create(@Valid @RequestBody UserCreateRequest request) {
        return ApiResponse.success(userService.create(request));
    }
    
    @GetMapping("/{id}")
    public ApiResponse<UserResponse> getById(@PathVariable UUID id) {
        return ApiResponse.success(userService.getById(id));
    }
    
    @GetMapping
    public ApiResponse<List<UserResponse>> getAll() {
        return ApiResponse.success(userService.getAll());
    }
    
    @PatchMapping("/{id}")
    public ApiResponse<UserResponse> updateUser(@PathVariable UUID id, @Valid @RequestBody UserUpdateRequest request) {
        return ApiResponse.success(userService.updateUserForId(id, request));
    }
}

