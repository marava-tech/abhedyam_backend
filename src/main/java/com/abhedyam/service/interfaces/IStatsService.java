package com.abhedyam.service.interfaces;

import com.abhedyam.dto.StatsRequest;
import com.abhedyam.dto.StatsResponse;

import java.time.LocalDate;
import java.util.List;

public interface IStatsService {
    void aggregateDailyStats(LocalDate date);
    void aggregateDailyStatsForDateRange(LocalDate startDate, LocalDate endDate);
    List<StatsResponse> getStats(StatsRequest request);
    void recomputeStats(LocalDate startDate, LocalDate endDate);
}

