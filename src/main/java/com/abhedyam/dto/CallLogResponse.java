package com.abhedyam.dto;

import com.abhedyam.model.CallLog;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CallLogResponse {
    private UUID id;
    private UUID ownerId;
    private UUID customerId;
    private String direction;
    private Instant startTime;
    private Instant endTime;
    private Integer durationSeconds;
    private String phone;
    private Instant createdAt;
    
    public static CallLogResponse fromEntity(CallLog callLog) {
        return new CallLogResponse(
            callLog.getId(),
            callLog.getOwnerId(),
            callLog.getCustomerId(),
            callLog.getDirection().name(),
            callLog.getStartTime(),
            callLog.getEndTime(),
            callLog.getDurationSeconds(),
            callLog.getPhone(),
            callLog.getCreatedAt()
        );
    }
}

