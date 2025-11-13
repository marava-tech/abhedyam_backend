package com.abhedyam.service;

import com.abhedyam.dto.AIJobStatusResponse;
import com.abhedyam.dto.SaleCreateRequest;
import com.abhedyam.dto.SaleItemRequest;
import com.abhedyam.exception.BusinessException;
import com.abhedyam.exception.ResourceNotFoundException;
import com.abhedyam.model.AIJob;
import com.abhedyam.model.Customer;
import com.abhedyam.model.Product;
import com.abhedyam.model.enums.AIJobStatus;
import com.abhedyam.repository.AIJobRepository;
import com.abhedyam.repository.CustomerRepository;
import com.abhedyam.repository.ProductRepository;
import com.abhedyam.service.interfaces.IAIInvoiceService;
import com.abhedyam.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AIInvoiceService implements IAIInvoiceService {
    
    private final AIJobRepository aiJobRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    
    @Override
    @Transactional
    public SaleCreateRequest createDraftSaleFromJob(UUID jobId, UUID customerId) {
        UUID ownerId = SecurityUtil.getCurrentUserId();
        
        AIJob job = aiJobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("AI job not found"));
        
        if (!job.getOwnerId().equals(ownerId)) {
            throw new BusinessException("UNAUTHORIZED", "You don't have access to this job");
        }
        
        if (job.getStatus() != AIJobStatus.COMPLETED) {
            throw new BusinessException("INVALID_JOB_STATUS", "Job must be completed to create draft sale");
        }
        
        if (job.getParsedData() == null || job.getParsedData().isEmpty()) {
            throw new BusinessException("NO_PARSED_DATA", "Job has no parsed data");
        }
        
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        
        if (customer.getOwnerId() != null && !customer.getOwnerId().equals(ownerId)) {
            throw new BusinessException("UNAUTHORIZED", "You don't have access to this customer");
        }
        
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            AIJobStatusResponse.ParsedInvoiceData parsedData = mapper.readValue(
                job.getParsedData(), 
                AIJobStatusResponse.ParsedInvoiceData.class
            );
            
            List<SaleItemRequest> items = new ArrayList<>();
            
            for (AIJobStatusResponse.ParsedInvoiceData.InvoiceItem item : parsedData.getItems()) {
                Product product = findOrCreateProduct(item.getProductCode(), item.getProductName(), item.getPrice());
                
                SaleItemRequest saleItem = new SaleItemRequest();
                saleItem.setProductId(product.getId());
                saleItem.setPrice(item.getPrice());
                saleItem.setQuantity(item.getQuantity() != null ? item.getQuantity() : BigDecimal.ONE);
                
                items.add(saleItem);
            }
            
            SaleCreateRequest saleRequest = new SaleCreateRequest();
            saleRequest.setCustomerId(customerId);
            saleRequest.setItems(items);
            
            job.setDraftSaleId(null);
            aiJobRepository.save(job);
            
            log.info("Created draft sale request from AI job: {}", jobId);
            
            return saleRequest;
        } catch (Exception e) {
            log.error("Error creating draft sale from job: {}", jobId, e);
            throw new BusinessException("PARSE_ERROR", "Failed to parse invoice data: " + e.getMessage());
        }
    }
    
    private Product findOrCreateProduct(String code, String name, BigDecimal price) {
        UUID ownerId = SecurityUtil.getCurrentUserId();
        
        List<Product> existingProducts = productRepository.findByOwnerId(ownerId).stream()
            .filter(p -> p.getCode() != null && p.getCode().equals(code))
            .toList();
        
        if (!existingProducts.isEmpty()) {
            return existingProducts.get(0);
        }
        
        Product product = new Product();
        product.setOwnerId(ownerId);
        product.setCode(code != null ? code : UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        product.setName(name != null ? name : "Unknown Product");
        product.setPrice(price != null ? price : BigDecimal.ZERO);
        product.setIsActive(true);
        product.setStock(BigDecimal.ZERO);
        
        return productRepository.save(product);
    }
}

