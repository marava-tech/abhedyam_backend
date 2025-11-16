package com.abhedyam.service;

import com.abhedyam.exception.BusinessException;
import com.abhedyam.exception.ResourceNotFoundException;
import com.abhedyam.model.Customer;
import com.abhedyam.model.SaleItem;
import com.abhedyam.repository.CustomerRepository;
import com.abhedyam.repository.SaleItemRepository;
import com.abhedyam.service.interfaces.ISaleItemService;
import com.abhedyam.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SaleItemService implements ISaleItemService {
    
    private final SaleItemRepository saleItemRepository;
    private final CustomerRepository customerRepository;
    
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
        UUID ownerId = SecurityUtil.getCurrentUserId();
        
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));
        
        if (customer.getOwnerId() == null || !customer.getOwnerId().equals(ownerId)) {
            throw new BusinessException("UNAUTHORIZED", "You don't have access to this customer's sale items");
        }
        
        return saleItemRepository.findByCustomerId(customerId).stream()
                .filter(item -> item.getOwnerId().equals(ownerId))
                .toList();
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
}

