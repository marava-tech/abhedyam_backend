package com.abhedyam.service;

import com.abhedyam.dto.UpiAccountCreateRequest;
import com.abhedyam.exception.BusinessException;
import com.abhedyam.exception.ResourceNotFoundException;
import com.abhedyam.model.Owner;
import com.abhedyam.model.UPIAccount;
import com.abhedyam.repository.OwnerRepository;
import com.abhedyam.repository.UPIAccountRepository;
import com.abhedyam.service.interfaces.IUpiAccountManagementService;
import com.abhedyam.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UpiAccountManagementService implements IUpiAccountManagementService {
    
    private final UPIAccountRepository upiAccountRepository;
    private final OwnerRepository ownerRepository;
    
    @Override
    @Transactional
    public UPIAccount createUpiAccount(UpiAccountCreateRequest request) {
        UUID ownerId = SecurityUtil.getCurrentUserId();
        
        if (upiAccountRepository.findByVpa(request.getVpa()).isPresent()) {
            throw new BusinessException("VPA_ALREADY_EXISTS", "VPA already exists");
        }
        
        UPIAccount upiAccount = new UPIAccount();
        upiAccount.setVpa(request.getVpa());
        upiAccount.setOwnerId(ownerId);
        upiAccount.setIsVerified(false);
        
        UPIAccount saved = upiAccountRepository.save(upiAccount);
        
        Owner owner = ownerRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found"));
        owner.setUpiAccountId(saved.getId());
        ownerRepository.save(owner);
        
        return saved;
    }
    
    @Override
    public List<UPIAccount> getOwnerUpiAccounts() {
        UUID ownerId = SecurityUtil.getCurrentUserId();
        return upiAccountRepository.findAll().stream()
                .filter(account -> account.getOwnerId().equals(ownerId))
                .toList();
    }
    
    @Override
    public UPIAccount getUpiAccountById(UUID id) {
        UUID ownerId = SecurityUtil.getCurrentUserId();
        UPIAccount account = upiAccountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("UPI Account not found"));
        
        if (!account.getOwnerId().equals(ownerId)) {
            throw new BusinessException("UNAUTHORIZED", "You don't have access to this UPI account");
        }
        
        return account;
    }
    
    @Override
    @Transactional
    public void deleteUpiAccount(UUID id) {
        UUID ownerId = SecurityUtil.getCurrentUserId();
        UPIAccount account = getUpiAccountById(id);
        
        account.setDeletedAt(Instant.now());
        account.setIsActive(false);
        upiAccountRepository.save(account);
        
        Owner owner = ownerRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found"));
        if (owner.getUpiAccountId() != null && owner.getUpiAccountId().equals(id)) {
            owner.setUpiAccountId(null);
            ownerRepository.save(owner);
        }
    }
    
    @Override
    @Transactional
    public UPIAccount setPrimaryUpiAccount(UUID id) {
        UUID ownerId = SecurityUtil.getCurrentUserId();
        UPIAccount account = getUpiAccountById(id);
        
        Owner owner = ownerRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found"));
        owner.setUpiAccountId(id);
        ownerRepository.save(owner);
        
        return account;
    }
}

