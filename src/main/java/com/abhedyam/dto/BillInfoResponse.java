package com.abhedyam.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Complete bill information response")
public class BillInfoResponse {
    
    @Schema(description = "Bill wrapper")
    private BillData bill;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BillData {
        @Schema(description = "Bill ID (transaction ID)", example = "bill_9f3a82d1")
        private String id;
        
        @Schema(description = "Bill number", example = "ABH-000124")
        private String billNumber;
        
        @Schema(description = "Bill date", example = "2026-01-13")
        private String billDate;
        
        @Schema(description = "Payment status", example = "PARTIALLY_PAID")
        private String status;
        
        @Schema(description = "Business information")
        private BusinessInfo business;
        
        @Schema(description = "Customer information")
        private CustomerInfo customer;
        
        @Schema(description = "Bill items")
        private List<BillItem> items;
        
        @Schema(description = "Amount details")
        private AmountDetails amounts;
        
        @Schema(description = "Payment history")
        private List<PaymentInfo> payments;
        
        @Schema(description = "Created timestamp")
        private Instant createdAt;
        
        @Schema(description = "Updated timestamp")
        private Instant updatedAt;
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
    public static class BillItem {
        @Schema(description = "Product ID", example = "prod_101")
        private String productId;
        
        @Schema(description = "Product name", example = "Desert Air Cooler")
        private String name;
        
        @Schema(description = "Quantity", example = "1")
        private Integer quantity;
        
        @Schema(description = "Unit price", example = "12500")
        private java.math.BigDecimal unitPrice;
        
        @Schema(description = "Total price", example = "12500")
        private java.math.BigDecimal totalPrice;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AmountDetails {
        @Schema(description = "Subtotal", example = "12500")
        private java.math.BigDecimal subtotal;
        
        @Schema(description = "Discount", example = "0")
        private java.math.BigDecimal discount;
        
        @Schema(description = "Total amount", example = "12500")
        private java.math.BigDecimal total;
        
        @Schema(description = "Paid amount", example = "5000")
        private java.math.BigDecimal paid;
        
        @Schema(description = "Pending amount", example = "7500")
        private java.math.BigDecimal pending;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentInfo {
        @Schema(description = "Payment ID", example = "pay_001")
        private String paymentId;
        
        @Schema(description = "Payment date", example = "2026-01-13")
        private String date;
        
        @Schema(description = "Payment amount", example = "3000")
        private java.math.BigDecimal amount;
        
        @Schema(description = "Payment mode", example = "CASH")
        private String mode;
    }
}

