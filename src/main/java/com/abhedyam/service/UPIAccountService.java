package com.abhedyam.service;

import com.abhedyam.exception.ResourceNotFoundException;
import com.abhedyam.model.UPIAccount;
import com.abhedyam.repository.UPIAccountRepository;
import com.abhedyam.service.interfaces.IUPIAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UPIAccountService implements IUPIAccountService {
    
    private final UPIAccountRepository upiAccountRepository;
    
    public UPIAccount create(UPIAccount upiAccount) {
        return upiAccountRepository.save(upiAccount);
    }
    
    public UPIAccount getById(UUID id) {
        return upiAccountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("UPIAccount not found with id: " + id));
    }
    
    public List<UPIAccount> getAll() {
        return upiAccountRepository.findAll();
    }
    
    @Transactional
    public UPIAccount update(UUID id, UPIAccount upiAccountDetails) {
        UPIAccount upiAccount = getById(id);
        if (upiAccountDetails.getVpa() != null) upiAccount.setVpa(upiAccountDetails.getVpa());
        if (upiAccountDetails.getIsVerified() != null) upiAccount.setIsVerified(upiAccountDetails.getIsVerified());
        if (upiAccountDetails.getVerifiedAt() != null) upiAccount.setVerifiedAt(upiAccountDetails.getVerifiedAt());
        return upiAccountRepository.save(upiAccount);
    }
    
    @Transactional
    public void delete(UUID id) {
        UPIAccount upiAccount = getById(id);
        upiAccount.setDeletedAt(Instant.now());
        upiAccount.setIsActive(false);
        upiAccountRepository.save(upiAccount);
    }
}

