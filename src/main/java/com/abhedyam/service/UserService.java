package com.abhedyam.service;

import com.abhedyam.dto.UserCreateRequest;
import com.abhedyam.dto.UserResponse;
import com.abhedyam.dto.UserUpdateRequest;
import com.abhedyam.exception.BusinessException;
import com.abhedyam.exception.ResourceNotFoundException;
import com.abhedyam.model.User;
import com.abhedyam.repository.UserRepository;
import com.abhedyam.service.interfaces.IUserService;
import com.abhedyam.util.EmailUtil;
import com.abhedyam.util.PhoneUtil;
import com.abhedyam.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {
    
    private final UserRepository userRepository;
    
    @Transactional
    public UserResponse create(UserCreateRequest request) {
        User user = new User();
        user.setName(request.getName());
        user.setType(request.getType() != null ? request.getType() : com.abhedyam.model.enums.UserType.BUSINESS);
        
        if (request.getPhone() != null && !request.getPhone().trim().isEmpty()) {
            String normalizedPhone = PhoneUtil.normalizePhone(request.getPhone());
            user.setPhone(PhoneUtil.extractPhoneWithoutCountryCode(normalizedPhone));
            user.setPhoneNormalized(normalizedPhone);
        }
        
        if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
            user.setEmail(EmailUtil.normalizeEmail(request.getEmail()));
        }
        
        if (request.getImageUrl() != null) {
            user.setImageUrl(request.getImageUrl());
        }
        
        validateEmailOrPhone(user);
        User saved = userRepository.save(user);
        return toResponse(saved);
    }
    
    public UserResponse getById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return toResponse(user);
    }
    
    public List<UserResponse> getAll() {
        return userRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public UserResponse updateCurrentUser(UserUpdateRequest request) {
        UUID id = SecurityUtil.getCurrentUserId();
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        
        if (request.getName() != null && !request.getName().trim().isEmpty()) {
            user.setName(request.getName());
        }
        
        if (request.getPhone() != null && !request.getPhone().trim().isEmpty()) {
            String normalizedPhone = PhoneUtil.normalizePhone(request.getPhone());
            user.setPhone(PhoneUtil.extractPhoneWithoutCountryCode(normalizedPhone));
            user.setPhoneNormalized(normalizedPhone);
        }
        
        if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
            user.setEmail(EmailUtil.normalizeEmail(request.getEmail()));
        }
        
        if (request.getImageUrl() != null) {
            if (request.getImageUrl().trim().isEmpty()) {
                user.setImageUrl(null);
            } else {
                user.setImageUrl(request.getImageUrl());
            }
        }
        
        if (request.getType() != null) {
            user.setType(request.getType());
        }
        
        validateEmailOrPhone(user);
        User saved = userRepository.save(user);
        return toResponse(saved);
    }
    
    private UserResponse toResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setPhone(user.getPhone());
        response.setEmail(user.getEmail());
        response.setType(user.getType());
        response.setImageUrl(user.getImageUrl());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());
        return response;
    }
    
    private void validateEmailOrPhone(User user) {
        boolean hasEmail = user.getEmail() != null && !user.getEmail().trim().isEmpty();
        boolean hasPhone = user.getPhone() != null && !user.getPhone().trim().isEmpty();
        
        if (!hasEmail && !hasPhone) {
            throw new BusinessException("MISSING_IDENTIFIER", "User must have either email or phone number");
        }
    }
}

