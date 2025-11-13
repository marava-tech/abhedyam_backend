package com.abhedyam.service;

import com.abhedyam.exception.ResourceNotFoundException;
import com.abhedyam.model.InventoryLedger;
import com.abhedyam.repository.InventoryLedgerRepository;
import com.abhedyam.service.interfaces.IInventoryLedgerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InventoryLedgerService implements IInventoryLedgerService {
    
    private final InventoryLedgerRepository inventoryLedgerRepository;
    
    public InventoryLedger create(InventoryLedger inventoryLedger) {
        return inventoryLedgerRepository.save(inventoryLedger);
    }
    
    public InventoryLedger getById(UUID id) {
        return inventoryLedgerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("InventoryLedger not found with id: " + id));
    }
    
    public List<InventoryLedger> getAll() {
        return inventoryLedgerRepository.findAll();
    }
    
    public List<InventoryLedger> getByOwnerId(UUID ownerId) {
        return inventoryLedgerRepository.findByOwnerId(ownerId);
    }
    
    public List<InventoryLedger> getByProductId(UUID productId) {
        return inventoryLedgerRepository.findByProductId(productId);
    }
    
    @Transactional
    public InventoryLedger update(UUID id, InventoryLedger ledgerDetails) {
        InventoryLedger ledger = getById(id);
        if (ledgerDetails.getChangeQty() != null) ledger.setChangeQty(ledgerDetails.getChangeQty());
        if (ledgerDetails.getBalanceAfter() != null) ledger.setBalanceAfter(ledgerDetails.getBalanceAfter());
        if (ledgerDetails.getSourceType() != null) ledger.setSourceType(ledgerDetails.getSourceType());
        if (ledgerDetails.getSourceId() != null) ledger.setSourceId(ledgerDetails.getSourceId());
        if (ledgerDetails.getNote() != null) ledger.setNote(ledgerDetails.getNote());
        return inventoryLedgerRepository.save(ledger);
    }
    
    @Transactional
    public void delete(UUID id) {
        InventoryLedger ledger = getById(id);
        ledger.setDeletedAt(Instant.now());
        ledger.setIsActive(false);
        inventoryLedgerRepository.save(ledger);
    }
}

