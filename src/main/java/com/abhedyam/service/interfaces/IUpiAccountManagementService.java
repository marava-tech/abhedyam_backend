package com.abhedyam.service.interfaces;

import com.abhedyam.dto.UpiAccountCreateRequest;
import com.abhedyam.dto.UpiAccountResponse;

import java.util.List;
import java.util.UUID;

public interface IUpiAccountManagementService {
    UpiAccountResponse createUpiAccount(UpiAccountCreateRequest request);
    UpiAccountResponse getCurrentUserUpiAccount();
    UpiAccountResponse getUpiAccountByOwnerId(UUID ownerId);
    UpiAccountResponse updateCurrentUserUpiAccount(UpiAccountCreateRequest request);
    List<UpiAccountResponse> getOwnerUpiAccounts();
    UpiAccountResponse getUpiAccountById(UUID id);
    UpiAccountResponse setPrimaryUpiAccount(UUID id);
    UpiAccountResponse verifyCurrentUserVpa();
}

