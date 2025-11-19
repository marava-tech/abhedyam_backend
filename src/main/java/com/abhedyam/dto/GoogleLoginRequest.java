package com.abhedyam.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Request to login with Google OAuth 2")
public class GoogleLoginRequest {
    @NotBlank(message = "Google ID token is required")
    @Schema(description = "Google ID token from OAuth 2 authentication", required = true, example = "eyJhbGciOiJSUzI1NiIsImtpZCI6IjE2MzU...")
    private String idToken;
}

