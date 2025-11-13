package com.abhedyam.service.interfaces;

import com.abhedyam.model.LocationDetails;

import java.util.List;
import java.util.UUID;

public interface ILocationDetailsService {
    LocationDetails create(LocationDetails locationDetails);
    LocationDetails getById(UUID id);
    List<LocationDetails> getAll();
    LocationDetails update(UUID id, LocationDetails locationDetails);
    void delete(UUID id);
}

