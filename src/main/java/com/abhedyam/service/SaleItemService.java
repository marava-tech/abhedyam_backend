package com.abhedyam.service;

import com.abhedyam.exception.ResourceNotFoundException;
import com.abhedyam.model.SaleItem;
import com.abhedyam.repository.SaleItemRepository;
import com.abhedyam.service.interfaces.ISaleItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SaleItemService implements ISaleItemService {
    
    private final SaleItemRepository saleItemRepository;
    
    public SaleItem create(SaleItem saleItem) {
        return saleItemRepository.save(saleItem);
    }
    
    public SaleItem getById(UUID id) {
        return saleItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SaleItem not found with id: " + id));
    }
    
    public List<SaleItem> getAll() {
        return saleItemRepository.findAll();
    }
    
    public List<SaleItem> getByOwnerId(UUID ownerId) {
        return saleItemRepository.findByOwnerId(ownerId);
    }
    
    public List<SaleItem> getByCustomerId(UUID customerId) {
        return saleItemRepository.findByCustomerId(customerId);
    }
    
    public List<SaleItem> getByTransactionId(String transactionId) {
        return saleItemRepository.findByTransactionId(transactionId);
    }
    
    @Transactional
    public SaleItem update(UUID id, SaleItem saleItemDetails) {
        SaleItem saleItem = getById(id);
        if (saleItemDetails.getPrice() != null) saleItem.setPrice(saleItemDetails.getPrice());
        if (saleItemDetails.getDueDate() != null) saleItem.setDueDate(saleItemDetails.getDueDate());
        if (saleItemDetails.getTransactionId() != null) saleItem.setTransactionId(saleItemDetails.getTransactionId());
        return saleItemRepository.save(saleItem);
    }
    
    @Transactional
    public void delete(UUID id) {
        SaleItem saleItem = getById(id);
        saleItem.setDeletedAt(Instant.now());
        saleItem.setIsActive(false);
        saleItemRepository.save(saleItem);
    }
}

