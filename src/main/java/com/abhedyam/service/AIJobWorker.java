package com.abhedyam.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class AIJobWorker {
    
    private final AIJobService aiJobService;
    private final RedisTemplate<String, String> redisTemplate;
    
    private static final String AI_JOB_QUEUE = "ai:jobs:queue";
    
    @Scheduled(fixedRate = 5000)
    public void processJobs() {
        String jobIdStr = redisTemplate.opsForList().leftPop(AI_JOB_QUEUE);
        
        if (jobIdStr == null) {
            return;
        }
        
        try {
            UUID jobId = UUID.fromString(jobIdStr);
            log.info("Processing AI job: {}", jobId);
            aiJobService.processJob(jobId);
        } catch (Exception e) {
            log.error("Error processing job from queue: {}", jobIdStr, e);
        }
    }
}

