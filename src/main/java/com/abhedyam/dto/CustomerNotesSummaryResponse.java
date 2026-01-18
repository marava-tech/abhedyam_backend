package com.abhedyam.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerNotesSummaryResponse {
    private UUID customerId;
    private Long totalNotes;
}


