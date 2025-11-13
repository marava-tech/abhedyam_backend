package com.abhedyam.service.interfaces;

import com.abhedyam.model.SaleItem;

import java.util.List;
import java.util.UUID;

public interface ISaleItemService {
    SaleItem create(SaleItem saleItem);
    SaleItem getById(UUID id);
    List<SaleItem> getAll();
    List<SaleItem> getByOwnerId(UUID ownerId);
    List<SaleItem> getByCustomerId(UUID customerId);
    SaleItem update(UUID id, SaleItem saleItemDetails);
    void delete(UUID id);
}

