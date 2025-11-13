package com.abhedyam.service.interfaces;

import com.abhedyam.dto.SaleCreateRequest;
import java.util.UUID;

public interface IAIInvoiceService {
    SaleCreateRequest createDraftSaleFromJob(UUID jobId, UUID customerId);
}

