package com.abhedyam.service;

import com.abhedyam.exception.BusinessException;
import com.abhedyam.exception.ResourceNotFoundException;
import com.abhedyam.util.SecurityUtil;

import java.util.UUID;
import java.util.function.Supplier;

public abstract class BaseService {
    
    protected UUID getCurrentOwnerId() {
        return SecurityUtil.getCurrentUserId();
    }
    
    protected <T> T validateOwnerAccess(T entity, String entityName) {
        if (entity == null) {
            throw new ResourceNotFoundException(entityName + " not found");
        }
        
        UUID currentOwnerId = getCurrentOwnerId();
        UUID entityOwnerId = getOwnerIdFromEntity(entity);
        
        if (entityOwnerId == null || !entityOwnerId.equals(currentOwnerId)) {
            throw new BusinessException("UNAUTHORIZED", 
                "You don't have permission to access this " + entityName.toLowerCase());
        }
        
        return entity;
    }
    
    protected <T> T findAndValidateOwnerAccess(Supplier<T> entitySupplier, String entityName) {
        T entity = entitySupplier.get();
        return validateOwnerAccess(entity, entityName);
    }
    
    protected void validateSameOwner(UUID targetOwnerId, String resourceName) {
        UUID currentOwnerId = getCurrentOwnerId();
        if (!currentOwnerId.equals(targetOwnerId)) {
            throw new BusinessException("UNAUTHORIZED", 
                "You can only access your own " + resourceName.toLowerCase());
        }
    }
    
    @SuppressWarnings("unchecked")
    private <T> UUID getOwnerIdFromEntity(T entity) {
        try {
            java.lang.reflect.Method getOwnerId = entity.getClass().getMethod("getOwnerId");
            Object ownerId = getOwnerId.invoke(entity);
            return ownerId instanceof UUID ? (UUID) ownerId : null;
        } catch (Exception e) {
            return null;
        }
    }
}

