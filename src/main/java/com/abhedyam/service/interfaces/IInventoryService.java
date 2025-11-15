package com.abhedyam.service.interfaces;

import com.abhedyam.model.Inventory;

import java.util.List;
import java.util.UUID;

public interface IInventoryService {
    Inventory create(Inventory inventory);
    Inventory getById(UUID id);
    List<Inventory> getAll();
    List<Inventory> getByOwnerId(UUID ownerId);
    Inventory update(UUID id, Inventory inventoryDetails);
}

