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
@Schema(description = "Receipt response - generated for each payment on a sale item")
public class ReceiptResponse {
    
    @Schema(description = "Receipt wrapper")
    private ReceiptData receipt;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReceiptData {
        @Schema(description = "Receipt ID", example = "rcpt_4b82a9f1")
        private String id;
        
        @Schema(description = "Receipt number", example = "ABH-RCPT-000341")
        private String receiptNumber;
        
        @Schema(description = "Receipt date", example = "2026-01-20")
        private String receiptDate;
        
        @Schema(description = "Invoice reference")
        private InvoiceReference invoice;
        
        @Schema(description = "Business information")
        private BusinessInfo business;
        
        @Schema(description = "Customer information")
        private CustomerInfo customer;
        
        @Schema(description = "Product information")
        private ProductInfo product;
        
        @Schema(description = "Current payment details")
        private CurrentPayment currentPayment;
        
        @Schema(description = "Payment history till now")
        private List<PaymentHistoryItem> paymentHistoryTillNow;
        
        @Schema(description = "Balance information")
        private BalanceInfo balance;
        
        @Schema(description = "Payment status", example = "PARTIALLY_PAID")
        private String status;
        
        @Schema(description = "Created timestamp")
        private Instant createdAt;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InvoiceReference {
        @Schema(description = "Invoice number", example = "ABH-INV-000124")
        private String invoiceNumber;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BusinessInfo {
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
        @Schema(description = "Customer name", example = "Ramesh Kumar")
        private String name;
        
        @Schema(description = "Customer village", example = "Pedaparimi")
        private String village;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductInfo {
        @Schema(description = "Product name", example = "Desert Air Cooler")
        private String name;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CurrentPayment {
        @Schema(description = "Amount paid", example = "2000")
        private BigDecimal amountPaid;
        
        @Schema(description = "Payment mode", example = "UPI")
        private String mode;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BalanceInfo {
        @Schema(description = "Pending amount", example = "7500")
        private BigDecimal pendingAmount;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentHistoryItem {
        @Schema(description = "Payment date and time", example = "2026-01-15 14:30:45")
        private String date;
        
        @Schema(description = "Payment amount", example = "3000")
        private BigDecimal amount;
        
        @Schema(description = "Payment mode", example = "CASH")
        private String mode;
    }
}

