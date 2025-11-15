package com.abhedyam.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Schema(description = "UPI account response")
public class UpiAccountResponse {
    @Schema(description = "UPI account ID", example = "3595381f-d038-4d6b-8fe0-dc76ebb7dde2")
    private UUID id;
    
    @Schema(description = "Virtual Payment Address", example = "user@paytm")
    private String vpa;
    
    @Schema(description = "Verification status", example = "false")
    private Boolean isVerified;
    
    @Schema(description = "Verification timestamp")
    private Instant verifiedAt;
    
    @Schema(description = "Creation timestamp")
    private Instant createdAt;
    
    @Schema(description = "Last update timestamp")
    private Instant updatedAt;
}

