package com.abhedyam.dto;

import com.abhedyam.model.AIJob;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AIJobStatusResponse {
    private UUID id;
    private UUID ownerId;
    private String status;
    private String fileName;
    private String fileType;
    private String fileUrl;
    private String errorMessage;
    private Instant processedAt;
    private UUID draftSaleId;
    private ParsedInvoiceData parsedData;
    private Instant createdAt;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ParsedInvoiceData {
        private String customerName;
        private String customerPhone;
        private java.math.BigDecimal totalAmount;
        private java.util.List<InvoiceItem> items;
        
        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class InvoiceItem {
            private String productName;
            private String productCode;
            private java.math.BigDecimal quantity;
            private java.math.BigDecimal price;
            private java.math.BigDecimal total;
        }
    }
    
    public static AIJobStatusResponse fromEntity(AIJob job, String fileUrl) {
        ParsedInvoiceData parsedData = null;
        if (job.getParsedData() != null && !job.getParsedData().isEmpty()) {
            try {
                com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                parsedData = mapper.readValue(job.getParsedData(), ParsedInvoiceData.class);
            } catch (Exception e) {
                // Ignore parsing errors
            }
        }
        
        return new AIJobStatusResponse(
            job.getId(),
            job.getOwnerId(),
            job.getStatus().name(),
            job.getFileName(),
            job.getFileType(),
            fileUrl,
            job.getErrorMessage(),
            job.getProcessedAt(),
            job.getDraftSaleId(),
            parsedData,
            job.getCreatedAt()
        );
    }
}

