package com.abhedyam.service.interfaces;

import com.abhedyam.dto.UserCreateRequest;
import com.abhedyam.dto.UserResponse;
import com.abhedyam.dto.UserUpdateRequest;

import java.util.List;
import java.util.UUID;

public interface IUserService {
    UserResponse create(UserCreateRequest request);
    UserResponse getById(UUID id);
    List<UserResponse> getAll();
    UserResponse updateCurrentUser(UserUpdateRequest request);
    UserResponse updateUserForId(UUID id, UserUpdateRequest request);
}

