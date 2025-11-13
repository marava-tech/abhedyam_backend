package com.abhedyam.service.interfaces;

import com.abhedyam.dto.UpiAccountCreateRequest;
import com.abhedyam.model.UPIAccount;

import java.util.List;
import java.util.UUID;

public interface IUpiAccountManagementService {
    UPIAccount createUpiAccount(UpiAccountCreateRequest request);
    List<UPIAccount> getOwnerUpiAccounts();
    UPIAccount getUpiAccountById(UUID id);
    void deleteUpiAccount(UUID id);
    UPIAccount setPrimaryUpiAccount(UUID id);
}

