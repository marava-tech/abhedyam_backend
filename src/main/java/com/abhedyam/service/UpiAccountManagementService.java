package com.abhedyam.service;

import com.abhedyam.dto.UpiAccountCreateRequest;
import com.abhedyam.dto.UpiAccountResponse;
import com.abhedyam.exception.BusinessException;
import com.abhedyam.exception.ResourceNotFoundException;
import com.abhedyam.model.Customer;
import com.abhedyam.model.UPIAccount;
import com.abhedyam.model.User;
import com.abhedyam.model.enums.UserType;
import com.abhedyam.repository.CustomerRepository;
import com.abhedyam.repository.UPIAccountRepository;
import com.abhedyam.repository.UserRepository;
import com.abhedyam.service.interfaces.IUpiAccountManagementService;
import com.abhedyam.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UpiAccountManagementService implements IUpiAccountManagementService {
    
    private final UPIAccountRepository upiAccountRepository;
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    
    @Override
    @Transactional
    public UpiAccountResponse createUpiAccount(UpiAccountCreateRequest request) {
        UUID ownerId = SecurityUtil.getCurrentUserId();
        
        if (upiAccountRepository.findByVpa(request.getVpa()).isPresent()) {
            throw new BusinessException("VPA_ALREADY_EXISTS", "VPA already exists");
        }
        
        UPIAccount existingAccount = upiAccountRepository.findByOwnerId(ownerId).orElse(null);
        UPIAccount upiAccount;
        
        if (existingAccount != null) {
            upiAccount = existingAccount;
        } else {
            upiAccount = new UPIAccount();
            upiAccount.setId(ownerId);
        }
        
        upiAccount.setVpa(request.getVpa());
        upiAccount.setOwnerId(ownerId);
        upiAccount.setIsVerified(false);
        
        UPIAccount saved = upiAccountRepository.save(upiAccount);
        return toResponse(saved);
    }
    
    @Override
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public UpiAccountResponse getCurrentUserUpiAccount() {
        UUID ownerId = SecurityUtil.getCurrentUserId();
        UPIAccount account = upiAccountRepository.findByOwnerId(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("UPI Account not found for current user"));
        return toResponse(account);
    }
    
    @Override
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public UpiAccountResponse getUpiAccountByOwnerId(UUID ownerId) {
        UUID currentUserId = SecurityUtil.getCurrentUserId();
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Current user not found"));
        
        boolean hasAccess = false;
        
        if (currentUser.getType() == UserType.BUSINESS && currentUserId.equals(ownerId)) {
            hasAccess = true;
        } else if (currentUser.getType() == UserType.CUSTOMER) {
            Customer customer = customerRepository.findById(currentUserId)
                    .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
            if (customer.getOwnerId() != null && customer.getOwnerId().equals(ownerId)) {
                hasAccess = true;
            }
        }
        
        if (!hasAccess) {
            throw new BusinessException("UNAUTHORIZED", "You don't have access to this owner's UPI account");
        }
        
        UPIAccount account = upiAccountRepository.findByOwnerId(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("UPI Account not found for owner"));
        return toResponse(account);
    }
    
    @Override
    @Transactional
    public UpiAccountResponse updateCurrentUserUpiAccount(UpiAccountCreateRequest request) {
        UUID ownerId = SecurityUtil.getCurrentUserId();
        
        if (upiAccountRepository.findByVpa(request.getVpa())
                .filter(acc -> !acc.getId().equals(ownerId))
                .isPresent()) {
            throw new BusinessException("VPA_ALREADY_EXISTS", "VPA already exists");
        }
        
        UPIAccount upiAccount = upiAccountRepository.findByOwnerId(ownerId)
                .orElseGet(() -> {
                    UPIAccount newAccount = new UPIAccount();
                    newAccount.setId(ownerId);
                    newAccount.setOwnerId(ownerId);
                    return newAccount;
                });
        
        upiAccount.setVpa(request.getVpa());
        upiAccount.setOwnerId(ownerId);
        
        UPIAccount saved = upiAccountRepository.save(upiAccount);
        return toResponse(saved);
    }
    
    @Override
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public List<UpiAccountResponse> getOwnerUpiAccounts() {
        UUID ownerId = SecurityUtil.getCurrentUserId();
        return upiAccountRepository.findByOwnerIdOrderByCreatedAtDesc(ownerId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public UpiAccountResponse getUpiAccountById(UUID id) {
        UUID ownerId = SecurityUtil.getCurrentUserId();
        UPIAccount account = upiAccountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("UPI Account not found"));
        
        if (!account.getOwnerId().equals(ownerId)) {
            throw new BusinessException("UNAUTHORIZED", "You don't have access to this UPI account");
        }
        
        return toResponse(account);
    }
    
    @Override
    @Transactional
    public UpiAccountResponse setPrimaryUpiAccount(UUID id) {
        UUID ownerId = SecurityUtil.getCurrentUserId();
        UPIAccount account = upiAccountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("UPI Account not found"));
        
        if (!account.getOwnerId().equals(ownerId)) {
            throw new BusinessException("UNAUTHORIZED", "You don't have access to this UPI account");
        }
        
        return toResponse(account);
    }
    
    @Override
    @Transactional
    public UpiAccountResponse verifyCurrentUserVpa() {
        UUID ownerId = SecurityUtil.getCurrentUserId();
        
        UPIAccount upiAccount = upiAccountRepository.findByOwnerId(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("UPI Account not found for current user"));
        
        upiAccount.setIsVerified(true);
        upiAccount.setVerifiedAt(Instant.now());
        
        UPIAccount saved = upiAccountRepository.save(upiAccount);
        return toResponse(saved);
    }
    
    private UpiAccountResponse toResponse(UPIAccount account) {
        UpiAccountResponse response = new UpiAccountResponse();
        response.setId(account.getId());
        response.setVpa(account.getVpa());
        response.setIsVerified(account.getIsVerified());
        response.setVerifiedAt(account.getVerifiedAt());
        response.setCreatedAt(account.getCreatedAt());
        response.setUpdatedAt(account.getUpdatedAt());
        return response;
    }
}

