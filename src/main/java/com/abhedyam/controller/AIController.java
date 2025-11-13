package com.abhedyam.controller;

import com.abhedyam.dto.AIJobStatusResponse;
import com.abhedyam.dto.ApiResponse;
import com.abhedyam.dto.SaleCreateRequest;
import com.abhedyam.model.AIJob;
import com.abhedyam.service.interfaces.IAIInvoiceService;
import com.abhedyam.service.interfaces.IAIJobService;
import com.abhedyam.service.interfaces.IStorageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/ai")
@RequiredArgsConstructor
public class AIController {
    
    private final IAIJobService aiJobService;
    private final IAIInvoiceService aiInvoiceService;
    private final IStorageService storageService;
    
    @PostMapping("/invoice/upload")
    @ResponseStatus(HttpStatus.CREATED)
    @com.abhedyam.annotation.RateLimited(maxRequests = 10, windowSeconds = 60, keyPrefix = "ai.upload")
    public ApiResponse<AIJobStatusResponse> uploadInvoice(@RequestParam("file") MultipartFile file) {
        AIJob job = aiJobService.createJob(file);
        String fileUrl = storageService.getFileUrl(job.getFilePath());
        return ApiResponse.success(AIJobStatusResponse.fromEntity(job, fileUrl));
    }
    
    @GetMapping("/jobs")
    public ApiResponse<List<AIJobStatusResponse>> getMyJobs() {
        List<AIJob> jobs = aiJobService.getMyJobs();
        List<AIJobStatusResponse> responses = jobs.stream()
            .map(job -> {
                String fileUrl = storageService.getFileUrl(job.getFilePath());
                return AIJobStatusResponse.fromEntity(job, fileUrl);
            })
            .collect(Collectors.toList());
        return ApiResponse.success(responses);
    }
    
    @GetMapping("/jobs/{id}")
    public ApiResponse<AIJobStatusResponse> getJobStatus(@PathVariable UUID id) {
        AIJob job = aiJobService.getJobStatus(id);
        String fileUrl = storageService.getFileUrl(job.getFilePath());
        return ApiResponse.success(AIJobStatusResponse.fromEntity(job, fileUrl));
    }
    
    @PostMapping("/jobs/{jobId}/create-draft-sale")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<SaleCreateRequest> createDraftSale(
            @PathVariable UUID jobId,
            @RequestParam UUID customerId) {
        SaleCreateRequest saleRequest = aiInvoiceService.createDraftSaleFromJob(jobId, customerId);
        return ApiResponse.success(saleRequest);
    }
}

