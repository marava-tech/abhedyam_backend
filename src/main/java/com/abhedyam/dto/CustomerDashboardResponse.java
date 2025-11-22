package com.abhedyam.dto;

import com.abhedyam.model.Product;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "Complete customer dashboard information")
public class CustomerDashboardResponse {
    @Schema(description = "Customer details")
    private CustomerResponse customer;
    
    @Schema(description = "Customer profile summary")
    private CustomerProfileSummary summary;
    
    @Schema(description = "Owner details")
    private OwnerResponse owner;
    
    @Schema(description = "Customer location details")
    private LocationDetailsResponse location;
    
    @Schema(description = "Sale records")
    private List<SaleItemResponse> saleRecords;
    
    @Schema(description = "Payment records")
    private List<PaymentResponse> payments;
    
    @Schema(description = "Products available from owner")
    private List<Product> products;
    
    @Schema(description = "Documents from owner")
    private List<DocumentResponse> documents;
}

