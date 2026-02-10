package com.abhedyam.service.interfaces;

import com.abhedyam.dto.UpiAccountCreateRequest;
import com.abhedyam.dto.UpiAccountResponse;

import java.util.UUID;

public interface IUpiAccountManagementService {
    UpiAccountResponse getCurrentUserUpiAccount();
    UpiAccountResponse getUpiAccountByOwnerId(UUID ownerId);
    UpiAccountResponse updateCurrentUserUpiAccount(UpiAccountCreateRequest request);
    UpiAccountResponse updateUpiAccountForOwner(UUID ownerId, UpiAccountCreateRequest request);
    UpiAccountResponse setPrimaryUpiAccount(UUID id);
    UpiAccountResponse verifyCurrentUserVpa();
    UpiAccountResponse verifyVpaForOwner(UUID ownerId);
}

