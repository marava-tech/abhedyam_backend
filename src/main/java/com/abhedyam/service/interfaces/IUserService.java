package com.abhedyam.service.interfaces;

import com.abhedyam.model.User;

import java.util.List;
import java.util.UUID;

public interface IUserService {
    User create(User user);
    User getById(UUID id);
    List<User> getAll();
    User update(UUID id, User userDetails);
    void delete(UUID id);
}

