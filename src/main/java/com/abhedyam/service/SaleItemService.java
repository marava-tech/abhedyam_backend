package com.abhedyam.service;

import com.abhedyam.dto.SaleItemResponse;
import com.abhedyam.exception.BusinessException;
import com.abhedyam.exception.ResourceNotFoundException;
import com.abhedyam.model.Customer;
import com.abhedyam.model.Product;
import com.abhedyam.model.SaleItem;
import com.abhedyam.model.User;
import com.abhedyam.model.enums.UserType;
import com.abhedyam.repository.CustomerRepository;
import com.abhedyam.repository.ProductRepository;
import com.abhedyam.repository.SaleItemRepository;
import com.abhedyam.repository.UserRepository;
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
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    
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
    
    @Transactional(readOnly = true)
    public List<SaleItemResponse> getByCustomerId(UUID customerId) {
        UUID currentUserId = SecurityUtil.getCurrentUserId();
        
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + customerId));
        
        UUID ownerId = customer.getOwnerId();
        
        // Allow access if:
        // 1. Current user is the customer themselves
        // 2. Current user is the owner of this customer
        boolean hasAccess = false;
        
        if (currentUserId.equals(customerId)) {
            hasAccess = true;
        } else if (ownerId != null) {
            User currentUser = userRepository.findById(currentUserId)
                    .orElseThrow(() -> new ResourceNotFoundException("Current user not found"));
            
            if (currentUser.getType() == UserType.BUSINESS && ownerId.equals(currentUserId)) {
                hasAccess = true;
            }
        }
        
        if (!hasAccess) {
            throw new BusinessException("UNAUTHORIZED", "You don't have access to this customer's sale items");
        }
        
        // If customer is accessing, use their ownerId; if owner is accessing, use currentUserId
        UUID filterOwnerId = currentUserId.equals(customerId) ? ownerId : currentUserId;
        
        List<SaleItem> saleItems = saleItemRepository.findByCustomerId(customerId).stream()
                .filter(item -> filterOwnerId != null && item.getOwnerId().equals(filterOwnerId))
                .toList();
        
        return saleItems.stream()
                .map(item -> {
                    Product product = productRepository.findById(item.getProductId())
                            .orElse(null);
                    String productName = product != null ? product.getName() : "Unknown";
                    
                    SaleItemResponse response = new SaleItemResponse();
                    response.setId(item.getId());
                    response.setProductId(item.getProductId());
                    response.setProductName(productName);
                    response.setCustomerId(item.getCustomerId());
                    response.setOwnerId(item.getOwnerId());
                    response.setPrice(item.getPrice());
                    response.setQuantity(item.getQuantity());
                    response.setRemainingAmount(item.getRemainingAmount());
                    response.setStatus(item.getStatus());
                    response.setDueDate(item.getDueDate());
                    response.setTransactionId(item.getTransactionId());
                    response.setCreatedAt(item.getCreatedAt());
                    response.setUpdatedAt(item.getUpdatedAt());
                    response.setIsActive(item.getIsActive());
                    
                    return response;
                })
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

