package com.abhedyam.util;

import com.abhedyam.exception.BusinessException;
import com.abhedyam.exception.ResourceNotFoundException;

import java.util.UUID;
import java.util.function.Supplier;

public class AuthorizationUtil {
    
    public static <T> T validateOwnerAccess(T entity, UUID ownerId, String entityName) {
        if (entity == null) {
            throw new ResourceNotFoundException(entityName + " not found");
        }
        
        UUID entityOwnerId = getOwnerId(entity);
        if (entityOwnerId == null || !entityOwnerId.equals(ownerId)) {
            throw new BusinessException("UNAUTHORIZED", "You don't have access to this " + entityName.toLowerCase());
        }
        
        return entity;
    }
    
    public static <T> T validateOwnerAccess(T entity, Supplier<T> entitySupplier, String entityName) {
        UUID ownerId = SecurityUtil.getCurrentUserId();
        T foundEntity = entitySupplier.get();
        return validateOwnerAccess(foundEntity, ownerId, entityName);
    }
    
    @SuppressWarnings("unchecked")
    private static <T> UUID getOwnerId(T entity) {
        try {
            java.lang.reflect.Method getOwnerId = entity.getClass().getMethod("getOwnerId");
            Object ownerId = getOwnerId.invoke(entity);
            return ownerId instanceof UUID ? (UUID) ownerId : null;
        } catch (Exception e) {
            return null;
        }
    }
    
    public static void validateSameOwner(UUID currentOwnerId, UUID targetOwnerId, String resourceName) {
        if (!currentOwnerId.equals(targetOwnerId)) {
            throw new BusinessException("UNAUTHORIZED", 
                "You can only access your own " + resourceName.toLowerCase());
        }
    }
}

