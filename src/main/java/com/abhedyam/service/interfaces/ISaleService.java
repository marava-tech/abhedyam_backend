package com.abhedyam.service.interfaces;

import com.abhedyam.dto.PageResponse;
import com.abhedyam.dto.SaleCreateRequest;
import com.abhedyam.dto.SaleDetailResponse;
import com.abhedyam.dto.SaleSearchRequest;
import com.abhedyam.model.SaleItem;

import java.util.List;
import java.util.UUID;

public interface ISaleService {
    SaleDetailResponse createSale(SaleCreateRequest request);
    SaleDetailResponse getSaleByTransactionId(String transactionId);
    PageResponse<SaleItem> searchSales(SaleSearchRequest request);
    List<SaleItem> getSaleItemsByTransactionId(String transactionId);
    SaleDetailResponse cancelSale(String transactionId);
}

