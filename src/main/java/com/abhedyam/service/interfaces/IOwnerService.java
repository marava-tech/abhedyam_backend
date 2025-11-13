package com.abhedyam.service.interfaces;

import com.abhedyam.model.Owner;

import java.util.List;
import java.util.UUID;

public interface IOwnerService {
    Owner create(Owner owner);
    Owner getById(UUID id);
    List<Owner> getAll();
    Owner update(UUID id, Owner ownerDetails);
    void delete(UUID id);
}

