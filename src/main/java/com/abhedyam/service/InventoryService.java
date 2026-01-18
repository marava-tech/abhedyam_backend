package com.abhedyam.service;

import com.abhedyam.exception.ResourceNotFoundException;
import com.abhedyam.model.Inventory;
import com.abhedyam.repository.InventoryRepository;
import com.abhedyam.service.interfaces.IInventoryService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService implements IInventoryService {
    
    private final InventoryRepository inventoryRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;
    
    private static final String INVENTORY_CACHE_PREFIX = "inventory:owner:";
    private static final int CACHE_TTL_MINUTES = 5;
    
    public Inventory create(Inventory inventory) {
        Inventory savedInventory = inventoryRepository.save(inventory);
        invalidateOwnerCaches(savedInventory.getOwnerId());
        return savedInventory;
    }
    
    public Inventory getById(UUID id) {
        return inventoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory could not be found"));
    }
    
    public List<Inventory> getAll() {
        return inventoryRepository.findAll();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Inventory> getByOwnerId(UUID ownerId) {
        String cacheKey = INVENTORY_CACHE_PREFIX + ownerId;
        
        try {
            String cached = redisTemplate.opsForValue().get(cacheKey);
            if (cached != null) {
                List<Inventory> cachedInventories = objectMapper.readValue(
                    cached,
                    new TypeReference<List<Inventory>>() {}
                );
                log.debug("Returning cached inventory for owner {}", ownerId);
                return cachedInventories;
            }
        } catch (Exception e) {
            log.warn("Error reading from cache for key: {}", cacheKey, e);
        }
        
        List<Inventory> inventories = inventoryRepository.findByOwnerId(ownerId);
        
        try {
            String jsonResponse = objectMapper.writeValueAsString(inventories);
            redisTemplate.opsForValue().set(cacheKey, jsonResponse, CACHE_TTL_MINUTES, TimeUnit.MINUTES);
            log.debug("Cached inventory for owner {}", ownerId);
        } catch (Exception e) {
            log.warn("Error caching inventory for key: {}", cacheKey, e);
        }
        
        return inventories;
    }
    
    @Transactional
    public Inventory update(UUID id, Inventory inventoryDetails) {
        Inventory inventory = getById(id);
        if (inventoryDetails.getStock() != null) inventory.setStock(inventoryDetails.getStock());
        Inventory updatedInventory = inventoryRepository.save(inventory);
        
        invalidateOwnerCaches(updatedInventory.getOwnerId());
        
        return updatedInventory;
    }
    
    public void invalidateOwnerCaches(UUID ownerId) {
        try {
            redisTemplate.delete(INVENTORY_CACHE_PREFIX + ownerId);
            log.debug("Invalidated inventory cache for owner {}", ownerId);
        } catch (Exception e) {
            log.warn("Error invalidating inventory cache for owner {}: {}", ownerId, e.getMessage());
        }
    }
}

