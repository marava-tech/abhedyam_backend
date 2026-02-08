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

import java.util.Map;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SaleItemService implements ISaleItemService {

    private final SaleItemRepository saleItemRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public SaleItem getById(UUID id) {
        return saleItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sale item could not be found"));
    }

    @Transactional(readOnly = true)
    public List<SaleItemResponse> getByCustomerId(UUID customerId) {
        UUID currentUserId = SecurityUtil.getCurrentUserId();

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer could not be found"));

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
            throw new BusinessException("UNAUTHORIZED",
                    "You don't have permission to access this customer's sale items");
        }

        // If customer is accessing, use their ownerId; if owner is accessing, use
        // currentUserId
        UUID filterOwnerId = currentUserId.equals(customerId) ? ownerId : currentUserId;

        List<SaleItem> saleItems = filterOwnerId == null
                ? List.of()
                : saleItemRepository.findByCustomerIdAndOwnerId(customerId, filterOwnerId);

        return mapSaleItems(saleItems, true);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SaleItemResponse> getByCustomerIdForOwner(UUID ownerId, UUID customerId, boolean expandProduct) {
        validateOwnerAccess(ownerId);
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer could not be found"));
        if (customer.getOwnerId() == null || !customer.getOwnerId().equals(ownerId)) {
            throw new BusinessException("UNAUTHORIZED",
                    "You don't have permission to access this customer's sale items");
        }
        List<SaleItem> saleItems = saleItemRepository.findByCustomerIdAndOwnerId(customerId, ownerId);
        return mapSaleItems(saleItems, expandProduct);
    }

    public List<SaleItem> getByTransactionId(String transactionId) {
        return saleItemRepository.findByTransactionId(transactionId);
    }

    private List<SaleItemResponse> mapSaleItems(List<SaleItem> saleItems, boolean expandProduct) {
        Map<UUID, String> productNameMap = Map.of();
        if (expandProduct && !saleItems.isEmpty()) {
            List<UUID> productIds = saleItems.stream()
                    .map(SaleItem::getProductId)
                    .distinct()
                    .toList();
            productNameMap = productRepository.findByIdIn(productIds).stream()
                    .collect(Collectors.toMap(Product::getId, Product::getName, (v1, v2) -> v1));
        }

        Map<UUID, String> finalProductNameMap = productNameMap;
        return saleItems.stream()
                .map(item -> {
                    SaleItemResponse response = new SaleItemResponse();
                    response.setId(item.getId());
                    response.setProductId(item.getProductId());
                    response.setProductName(expandProduct ? finalProductNameMap.get(item.getProductId()) : null);
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

    private void validateOwnerAccess(UUID ownerId) {
        UUID currentOwnerId = SecurityUtil.getCurrentUserId();
        if (ownerId == null || !ownerId.equals(currentOwnerId)) {
            throw new BusinessException("UNAUTHORIZED", "You can only access your own sale items");
        }
    }
}
