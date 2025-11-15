package com.abhedyam.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Schema(description = "UPI account response")
public class UpiAccountResponse {
    @Schema(description = "UPI account ID (read-only, system-generated)", example = "3595381f-d038-4d6b-8fe0-dc76ebb7dde2", accessMode = Schema.AccessMode.READ_ONLY)
    private UUID id;
    
    @Schema(description = "Virtual Payment Address", example = "user@paytm")
    private String vpa;
    
    @Schema(description = "Verification status", example = "false")
    private Boolean isVerified;
    
    @Schema(description = "Verification timestamp (read-only, system-generated)", accessMode = Schema.AccessMode.READ_ONLY)
    private Instant verifiedAt;
    
    @Schema(description = "Creation timestamp (read-only, system-generated)", example = "2025-11-15T10:51:15.325Z", accessMode = Schema.AccessMode.READ_ONLY)
    private Instant createdAt;
    
    @Schema(description = "Last update timestamp (read-only, system-generated)", example = "2025-11-15T10:51:15.325Z", accessMode = Schema.AccessMode.READ_ONLY)
    private Instant updatedAt;
}

