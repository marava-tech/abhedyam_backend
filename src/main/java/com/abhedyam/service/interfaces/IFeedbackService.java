package com.abhedyam.service.interfaces;

import com.abhedyam.dto.FeedbackCreateRequest;
import com.abhedyam.model.Feedback;

import java.util.List;
import java.util.UUID;

public interface IFeedbackService {
    Feedback create(FeedbackCreateRequest request);
    List<Feedback> getByUserId(UUID userId);
}

