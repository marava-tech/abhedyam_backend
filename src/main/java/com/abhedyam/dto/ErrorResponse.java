package com.abhedyam.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private String error;
    private String message;
    private String code;
    private Instant timestamp;
    private String correlationId;
    private String path;
    
    public ErrorResponse(String error, String message, String code, String correlationId, String path) {
        this.error = error;
        this.message = message;
        this.code = code;
        this.timestamp = Instant.now();
        this.correlationId = correlationId;
        this.path = path;
    }
}

