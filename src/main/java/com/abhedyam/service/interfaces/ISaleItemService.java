package com.abhedyam.service.interfaces;

import com.abhedyam.dto.SaleItemResponse;
import com.abhedyam.model.SaleItem;

import java.util.List;
import java.util.UUID;

public interface ISaleItemService {
    SaleItem getById(UUID id);

    List<SaleItemResponse> getByCustomerId(UUID customerId);

    List<SaleItemResponse> getByCustomerIdForOwner(UUID ownerId, UUID customerId, boolean expandProduct);
}
