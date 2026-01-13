package com.abhedyam.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Invoice response - generated at the start of sale")
public class InvoiceResponse {
    
    @Schema(description = "Invoice wrapper")
    private InvoiceData invoice;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InvoiceData {
        @Schema(description = "Invoice ID (transaction ID)", example = "inv_9f3a82d1")
        private String id;
        
        @Schema(description = "Invoice number", example = "ABH-INV-000124")
        private String invoiceNumber;
        
        @Schema(description = "Invoice date", example = "2026-01-13")
        private String invoiceDate;
        
        @Schema(description = "Invoice status", example = "PENDING")
        private String status;
        
        @Schema(description = "Business information")
        private BusinessInfo business;
        
        @Schema(description = "Customer information")
        private CustomerInfo customer;
        
        @Schema(description = "Invoice items")
        private List<InvoiceItem> items;
        
        @Schema(description = "Amount details")
        private AmountDetails amounts;
        
        @Schema(description = "Credit sale information")
        private CreditInfo credit;
        
        @Schema(description = "Created timestamp")
        private Instant createdAt;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AmountDetails {
        @Schema(description = "Total amount", example = "12500")
        private BigDecimal total;
        
        @Schema(description = "Amount paid so far", example = "5000")
        private BigDecimal paidAmount;
        
        @Schema(description = "Pending amount", example = "7500")
        private BigDecimal pendingAmount;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreditInfo {
        @Schema(description = "Is credit sale", example = "true")
        private Boolean isCreditSale;
        
        @Schema(description = "Due date", example = "2026-02-15")
        private String dueDate;
        
        @Schema(description = "Credit sale note")
        private String note;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BusinessInfo {
        @Schema(description = "Business ID", example = "biz_001")
        private String id;
        
        @Schema(description = "Business name", example = "Sri Lakshmi Enterprises")
        private String name;
        
        @Schema(description = "Business phone", example = "+91-9XXXXXXXXX")
        private String phone;
        
        @Schema(description = "Business address", example = "123 Main Street, Bangalore")
        private String address;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CustomerInfo {
        @Schema(description = "Customer ID", example = "cust_2031")
        private String id;
        
        @Schema(description = "Customer name", example = "Ramesh Kumar")
        private String name;
        
        @Schema(description = "Customer phone", example = "+91-9XXXXXXXXX")
        private String phone;
        
        @Schema(description = "Customer village", example = "Pedaparimi")
        private String village;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InvoiceItem {
        @Schema(description = "Product ID", example = "prod_101")
        private String productId;
        
        @Schema(description = "Product name", example = "Desert Air Cooler")
        private String name;
        
        @Schema(description = "Quantity", example = "1")
        private Integer quantity;
        
        @Schema(description = "Unit price", example = "12500")
        private BigDecimal unitPrice;
        
        @Schema(description = "Total price", example = "12500")
        private BigDecimal totalPrice;
    }
}

