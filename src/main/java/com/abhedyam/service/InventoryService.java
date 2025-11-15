package com.abhedyam.service;

import com.abhedyam.exception.ResourceNotFoundException;
import com.abhedyam.model.Inventory;
import com.abhedyam.repository.InventoryRepository;
import com.abhedyam.service.interfaces.IInventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InventoryService implements IInventoryService {
    
    private final InventoryRepository inventoryRepository;
    
    public Inventory create(Inventory inventory) {
        return inventoryRepository.save(inventory);
    }
    
    public Inventory getById(UUID id) {
        return inventoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found with id: " + id));
    }
    
    public List<Inventory> getAll() {
        return inventoryRepository.findAll();
    }
    
    public List<Inventory> getByOwnerId(UUID ownerId) {
        return inventoryRepository.findByOwnerId(ownerId);
    }
    
    @Transactional
    public Inventory update(UUID id, Inventory inventoryDetails) {
        Inventory inventory = getById(id);
        if (inventoryDetails.getStock() != null) inventory.setStock(inventoryDetails.getStock());
        return inventoryRepository.save(inventory);
    }
}

