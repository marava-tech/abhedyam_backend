package com.abhedyam.service.interfaces;

import com.abhedyam.dto.AIJobCreateRequest;
import com.abhedyam.model.AIJob;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface IAIJobService {
    AIJob createJob(MultipartFile file);
    AIJob getJobById(UUID id);
    List<AIJob> getMyJobs();
    AIJob getJobStatus(UUID id);
    void processJob(UUID jobId);
}

