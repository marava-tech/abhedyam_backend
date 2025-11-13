package com.abhedyam.controller;

import com.abhedyam.dto.ApiResponse;
import com.abhedyam.model.User;
import com.abhedyam.service.interfaces.IUserService;
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
    public ApiResponse<User> create(@RequestBody User user) {
        return ApiResponse.success(userService.create(user));
    }
    
    @GetMapping("/{id}")
    public ApiResponse<User> getById(@PathVariable UUID id) {
        return ApiResponse.success(userService.getById(id));
    }
    
    @GetMapping
    public ApiResponse<List<User>> getAll() {
        return ApiResponse.success(userService.getAll());
    }
    
    @PutMapping("/{id}")
    public ApiResponse<User> update(@PathVariable UUID id, @RequestBody User user) {
        return ApiResponse.success(userService.update(id, user));
    }
    
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResponse<Void> delete(@PathVariable UUID id) {
        userService.delete(id);
        return ApiResponse.success(null);
    }
}

