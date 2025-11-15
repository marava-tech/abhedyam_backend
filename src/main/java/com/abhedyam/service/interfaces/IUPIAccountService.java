package com.abhedyam.service.interfaces;

import com.abhedyam.model.UPIAccount;

import java.util.List;
import java.util.UUID;

public interface IUPIAccountService {
    UPIAccount create(UPIAccount upiAccount);
    UPIAccount getById(UUID id);
    List<UPIAccount> getAll();
    UPIAccount update(UUID id, UPIAccount upiAccountDetails);
}

