package com.abhedyam.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class BulkImportResult {
    private int customersCreated;
    private int salesImported;
    private int paymentsRecorded;
    private int rowsSkipped;
    private List<String> errors;
}
