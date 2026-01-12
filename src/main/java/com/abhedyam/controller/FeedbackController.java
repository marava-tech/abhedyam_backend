package com.abhedyam.controller;

import com.abhedyam.dto.ApiResponse;
import com.abhedyam.dto.FeedbackCreateRequest;
import com.abhedyam.model.Feedback;
import com.abhedyam.service.interfaces.IFeedbackService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/feedbacks")
@RequiredArgsConstructor
@Tag(name = "Feedback", description = "User feedback submission endpoints")
public class FeedbackController {
    
    private final IFeedbackService feedbackService;
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Submit feedback", description = "Submit feedback or an issue with an optional screenshot")
    public ApiResponse<Feedback> create(@Valid @RequestBody FeedbackCreateRequest request) {
        return ApiResponse.success(feedbackService.create(request));
    }
    
    @GetMapping("/user/{userId}")
    @Operation(summary = "Get feedback by user", description = "Fetch feedback entries for the specified user")
    public ApiResponse<List<Feedback>> getByUserId(@PathVariable UUID userId) {
        return ApiResponse.success(feedbackService.getByUserId(userId));
    }
}

