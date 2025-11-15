package com.abhedyam.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Complete owner details including location, settings, and UPI account")
public class OwnerDetailsResponse {
    @Schema(description = "Owner basic information")
    private OwnerResponse owner;
    
    @Schema(description = "Location details (if available)")
    private LocationDetailsResponse location;
    
    @Schema(description = "Owner settings (if available)")
    private OwnerSettingsResponse settings;
    
    @Schema(description = "UPI account details (if available)")
    private UpiAccountResponse upiAccount;
}

