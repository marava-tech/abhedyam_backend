package com.abhedyam.dto;

import com.abhedyam.model.enums.ReminderChannel;
import com.abhedyam.model.enums.ReminderType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Schema(description = "Request to create a reminder", example = SwaggerExamples.REMINDER_CREATE_REQUEST)
public class ReminderCreateRequest {
    @NotNull(message = "Customer ID is required")
    @Schema(description = "UUID of the customer", example = "123e4567-e89b-12d3-a456-426614174000", required = true)
    private UUID customerId;
    
    @NotBlank(message = "Reminder name is required")
    @Schema(description = "Reminder name", example = "Follow up call", required = true)
    private String name;
    
    @NotNull(message = "Reminder type is required")
    @Schema(description = "Type of reminder", example = "FOLLOW_UP", required = true)
    private ReminderType type;
    
    @NotNull(message = "Channel is required")
    @Schema(description = "Notification channel", example = "SMS", required = true)
    private ReminderChannel channel;
    
    @NotNull(message = "Due time is required")
    @Schema(description = "Due date and time", example = "2024-12-31T10:00:00Z", required = true)
    private Instant dueAt;
    
    @NotBlank(message = "Reminder text is required")
    @Schema(description = "Reminder message text", example = "Call customer about payment", required = true)
    private String text;
}
