package com.abhedyam.service;

import com.abhedyam.dto.AIJobStatusResponse;
import com.abhedyam.exception.BusinessException;
import com.abhedyam.exception.ResourceNotFoundException;
import com.abhedyam.model.AIJob;
import com.abhedyam.model.enums.AIJobStatus;
import com.abhedyam.repository.AIJobRepository;
import com.abhedyam.service.interfaces.IAIJobService;
import com.abhedyam.service.interfaces.IStorageService;
import com.abhedyam.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AIJobService implements IAIJobService {
    
    private final AIJobRepository aiJobRepository;
    private final IStorageService storageService;
    private final RedisTemplate<String, String> redisTemplate;
    
    private static final String AI_JOB_QUEUE = "ai:jobs:queue";
    
    @Override
    @Transactional
    public AIJob createJob(MultipartFile file) {
        UUID ownerId = SecurityUtil.getCurrentUserId();
        
        if (file.isEmpty()) {
            throw new BusinessException("INVALID_FILE", "File is empty");
        }
        
        String contentType = file.getContentType();
        if (contentType == null || (!contentType.startsWith("image/") && !contentType.equals("application/pdf"))) {
            throw new BusinessException("INVALID_FILE_TYPE", "Only images and PDF files are supported");
        }
        
        String filePath = storageService.saveFile(file, "invoices");
        String fileUrl = storageService.getFileUrl(filePath);
        
        AIJob job = new AIJob();
        job.setOwnerId(ownerId);
        job.setStatus(AIJobStatus.PENDING);
        job.setFilePath(filePath);
        job.setFileName(file.getOriginalFilename());
        job.setFileType(contentType);
        
        AIJob savedJob = aiJobRepository.save(job);
        
        redisTemplate.opsForList().rightPush(AI_JOB_QUEUE, savedJob.getId().toString());
        
        log.info("AI job created: {} for file: {}", savedJob.getId(), file.getOriginalFilename());
        
        return savedJob;
    }
    
    @Override
    public AIJob getJobById(UUID id) {
        UUID ownerId = SecurityUtil.getCurrentUserId();
        AIJob job = aiJobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AI job not found"));
        
        if (!job.getOwnerId().equals(ownerId)) {
            throw new BusinessException("UNAUTHORIZED", "You don't have access to this job");
        }
        
        return job;
    }
    
    @Override
    public List<AIJob> getMyJobs() {
        UUID ownerId = SecurityUtil.getCurrentUserId();
        return aiJobRepository.findByOwnerId(ownerId);
    }
    
    @Override
    public AIJob getJobStatus(UUID id) {
        return getJobById(id);
    }
    
    @Override
    @Transactional
    public void processJob(UUID jobId) {
        AIJob job = aiJobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("AI job not found"));
        
        if (job.getStatus() != AIJobStatus.PENDING) {
            log.warn("Job {} is not in PENDING status, skipping", jobId);
            return;
        }
        
        job.setStatus(AIJobStatus.PROCESSING);
        aiJobRepository.save(job);
        
        try {
            String parsedData = processInvoice(job);
            job.setParsedData(parsedData);
            job.setStatus(AIJobStatus.COMPLETED);
            job.setProcessedAt(java.time.Instant.now());
            
            log.info("AI job completed: {}", jobId);
        } catch (Exception e) {
            log.error("Error processing AI job: {}", jobId, e);
            job.setStatus(AIJobStatus.FAILED);
            job.setErrorMessage(e.getMessage());
        }
        
        aiJobRepository.save(job);
    }
    
    private String processInvoice(AIJob job) {
        // TODO: Integrate with actual OCR/AI service (e.g., Tesseract, Google Vision API, etc.)
        // For now, return mock data structure
        
        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
        try {
            AIJobStatusResponse.ParsedInvoiceData mockData = new AIJobStatusResponse.ParsedInvoiceData();
            mockData.setCustomerName("Sample Customer");
            mockData.setCustomerPhone("+1234567890");
            mockData.setTotalAmount(java.math.BigDecimal.valueOf(1000.00));
            
            AIJobStatusResponse.ParsedInvoiceData.InvoiceItem item = new AIJobStatusResponse.ParsedInvoiceData.InvoiceItem();
            item.setProductName("Sample Product");
            item.setProductCode("PROD001");
            item.setQuantity(java.math.BigDecimal.ONE);
            item.setPrice(java.math.BigDecimal.valueOf(1000.00));
            item.setTotal(java.math.BigDecimal.valueOf(1000.00));
            
            mockData.setItems(java.util.List.of(item));
            
            return mapper.writeValueAsString(mockData);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize parsed data", e);
        }
    }
}

