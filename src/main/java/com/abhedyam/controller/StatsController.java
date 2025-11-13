package com.abhedyam.controller;

import com.abhedyam.dto.ApiResponse;
import com.abhedyam.dto.StatsRequest;
import com.abhedyam.dto.StatsResponse;
import com.abhedyam.service.interfaces.IStatsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/stats")
@RequiredArgsConstructor
public class StatsController {
    
    private final IStatsService statsService;
    
    @GetMapping
    public ApiResponse<List<StatsResponse>> getStats(@ModelAttribute StatsRequest request) {
        if (request.getStartDate() == null) {
            request.setStartDate(LocalDate.now().minusDays(30));
        }
        if (request.getEndDate() == null) {
            request.setEndDate(LocalDate.now());
        }
        return ApiResponse.success(statsService.getStats(request));
    }
    
    @PostMapping("/recompute")
    public ApiResponse<Void> recomputeStats(@Valid @RequestBody StatsRequest request) {
        LocalDate startDate = request.getStartDate() != null ? request.getStartDate() : LocalDate.now().minusDays(30);
        LocalDate endDate = request.getEndDate() != null ? request.getEndDate() : LocalDate.now();
        
        statsService.recomputeStats(startDate, endDate);
        return ApiResponse.success(null);
    }
}

