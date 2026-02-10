package com.abhedyam.service.interfaces;

import com.abhedyam.dto.SaleCreateRequest;
import com.abhedyam.dto.SaleDetailResponse;

public interface ISaleService {
    SaleDetailResponse createSale(SaleCreateRequest request);
    SaleDetailResponse getSaleByTransactionId(String transactionId);
    SaleDetailResponse cancelSale(String transactionId);
}

