package com.abhedyam.service.interfaces;

import com.abhedyam.model.InventoryLedger;

import java.util.List;
import java.util.UUID;

public interface IInventoryLedgerService {
    InventoryLedger create(InventoryLedger inventoryLedger);
    InventoryLedger getById(UUID id);
    List<InventoryLedger> getAll();
    List<InventoryLedger> getByOwnerId(UUID ownerId);
    List<InventoryLedger> getByProductId(UUID productId);
    InventoryLedger update(UUID id, InventoryLedger ledgerDetails);
    void delete(UUID id);
}

