package com.abhedyam.service.interfaces;

import com.abhedyam.dto.ProfileUpdateRequest;
import com.abhedyam.model.Owner;
import com.abhedyam.model.User;

import java.util.UUID;

public interface IProfileService {
    User getCurrentUserProfile();
    Owner getCurrentOwnerProfile();
    User updateProfile(ProfileUpdateRequest request);
}

