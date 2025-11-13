package com.abhedyam.service;

import com.abhedyam.exception.ResourceNotFoundException;
import com.abhedyam.model.User;
import com.abhedyam.repository.UserRepository;
import com.abhedyam.service.interfaces.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {
    
    private final UserRepository userRepository;
    
    public User create(User user) {
        return userRepository.save(user);
    }
    
    public User getById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }
    
    public List<User> getAll() {
        return userRepository.findAll();
    }
    
    @Transactional
    public User update(UUID id, User userDetails) {
        User user = getById(id);
        if (userDetails.getName() != null) user.setName(userDetails.getName());
        if (userDetails.getPhone() != null) user.setPhone(userDetails.getPhone());
        if (userDetails.getPhoneNormalized() != null) user.setPhoneNormalized(userDetails.getPhoneNormalized());
        if (userDetails.getType() != null) user.setType(userDetails.getType());
        if (userDetails.getImageUrl() != null) user.setImageUrl(userDetails.getImageUrl());
        return userRepository.save(user);
    }
    
    @Transactional
    public void delete(UUID id) {
        User user = getById(id);
        user.setDeletedAt(Instant.now());
        user.setIsActive(false);
        userRepository.save(user);
    }
}

