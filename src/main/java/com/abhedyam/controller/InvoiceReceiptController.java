package com.abhedyam.controller;

import com.abhedyam.dto.ApiResponse;
import com.abhedyam.dto.InvoiceResponse;
import com.abhedyam.dto.ReceiptResponse;
import com.abhedyam.service.InvoiceReceiptService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Invoices & Receipts", description = "API for retrieving invoices and receipts")
public class InvoiceReceiptController {
    
    private final InvoiceReceiptService invoiceReceiptService;
    
    @GetMapping("/invoices/customer/{customerId}")
    @Operation(
        summary = "Get invoice by customer ID",
        description = "Returns invoice generated at the start of sale. Shows products, total amount, customer, due date, and credit sale note. If saleItemId is not provided, uses customer's first sale item. Does NOT show payment history or paid/pending breakdown. Accessible only by the customer or their owner."
    )
    public ApiResponse<InvoiceResponse> getInvoiceByCustomerId(
            @PathVariable UUID customerId,
            @RequestParam(required = false) UUID saleItemId) {
        return ApiResponse.success(invoiceReceiptService.getInvoiceByCustomerId(customerId, saleItemId));
    }
    
    @GetMapping("/receipts/customer/{customerId}")
    @Operation(
        summary = "Get receipt by customer ID",
        description = "Returns receipt for a sale item. If saleItemId is not provided, uses customer's first sale item. Shows current payment, all payment history for this sale item, pending amount, and invoice reference. Accessible only by the customer or their owner."
    )
    public ApiResponse<ReceiptResponse> getReceiptByCustomerId(
            @PathVariable UUID customerId,
            @RequestParam(required = false) UUID saleItemId) {
        return ApiResponse.success(invoiceReceiptService.getReceiptByCustomerId(customerId, saleItemId));
    }
}

