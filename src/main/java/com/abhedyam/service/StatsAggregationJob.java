package com.abhedyam.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
@Slf4j
public class StatsAggregationJob {
    
    private final StatsService statsService;
    
    @Scheduled(cron = "0 0 1 * * ?")
    public void aggregateYesterdayStats() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        log.info("Starting daily stats aggregation for date: {}", yesterday);
        
        try {
            statsService.aggregateDailyStats(yesterday);
            log.info("Daily stats aggregation completed for date: {}", yesterday);
        } catch (Exception e) {
            log.error("Error aggregating daily stats for date: {}", yesterday, e);
        }
    }
}

