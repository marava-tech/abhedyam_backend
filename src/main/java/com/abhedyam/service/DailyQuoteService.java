package com.abhedyam.service;

import com.abhedyam.dto.DailyQuoteCreateRequest;
import com.abhedyam.exception.ResourceNotFoundException;
import com.abhedyam.model.DailyQuote;
import com.abhedyam.repository.DailyQuoteRepository;
import com.abhedyam.service.interfaces.IDailyQuoteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class DailyQuoteService implements IDailyQuoteService {
    
    private final DailyQuoteRepository dailyQuoteRepository;
    private final RedisTemplate<String, String> redisTemplate;
    
    private static final String QUOTE_KEY_PREFIX = "daily-quote:";
    private static final ZoneId IST_ZONE = ZoneId.of("Asia/Kolkata");
    
    @Override
    @Transactional
    public DailyQuote create(DailyQuoteCreateRequest request) {
        DailyQuote dailyQuote = new DailyQuote();
        dailyQuote.setText(request.getText());
        dailyQuote.setIsActive(true);
        return dailyQuoteRepository.save(dailyQuote);
    }
    
    public DailyQuote getById(UUID id) {
        return dailyQuoteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("DailyQuote not found with id: " + id));
    }
    
    public List<DailyQuote> getAll() {
        return dailyQuoteRepository.findAll();
    }
    
    public List<DailyQuote> getActiveQuotes() {
        return dailyQuoteRepository.findByIsActiveTrue();
    }
    
    @Override
    @Transactional
    public DailyQuote getTodaysQuote() {
        LocalDate today = LocalDate.now(IST_ZONE);
        String todayKey = QUOTE_KEY_PREFIX + today.toString();
        
        String cachedQuoteData = redisTemplate.opsForValue().get(todayKey);
        
        if (cachedQuoteData != null) {
            try {
                String[] parts = cachedQuoteData.split("\\|", 2);
                if (parts.length == 2) {
                    UUID quoteId = UUID.fromString(parts[0]);
                    String quoteText = parts[1];
                    
                    DailyQuote cachedQuote = new DailyQuote();
                    cachedQuote.setId(quoteId);
                    cachedQuote.setText(quoteText);
                    cachedQuote.setIsActive(true);
                    cachedQuote.setCreatedAt(Instant.now());
                    cachedQuote.setUpdatedAt(Instant.now());
                    
                    log.info("Returning cached today's quote for {}: {} (no DB call)", today, quoteId);
                    return cachedQuote;
                }
            } catch (Exception e) {
                log.warn("Error parsing cached quote data, selecting new one", e);
            }
        }
        
        List<DailyQuote> unusedQuotes = dailyQuoteRepository.findUnusedActiveQuotes();
        DailyQuote selectedQuote;
        
        if (!unusedQuotes.isEmpty()) {
            selectedQuote = unusedQuotes.get(0);
            log.info("Selected unused quote for {}: {} (ID: {})", 
                today,
                selectedQuote.getText().substring(0, Math.min(50, selectedQuote.getText().length())), 
                selectedQuote.getId());
        } else {
            List<DailyQuote> usedQuotes = dailyQuoteRepository.findUsedActiveQuotesOrderByLastUsedAsc();
            if (usedQuotes.isEmpty()) {
                throw new ResourceNotFoundException("No active quotes available");
            }
            selectedQuote = usedQuotes.get(0);
            log.info("Selected quote with oldest lastUsedAt for {}: {} (ID: {})", 
                today,
                selectedQuote.getText().substring(0, Math.min(50, selectedQuote.getText().length())), 
                selectedQuote.getId());
        }
        selectedQuote.setLastUsedAt(Instant.now());
        DailyQuote savedQuote = dailyQuoteRepository.save(selectedQuote);
        
        try {
            long secondsUntilMidnight = getSecondsUntilMidnightIST();
            String cacheValue = savedQuote.getId().toString() + "|" + savedQuote.getText();
            redisTemplate.opsForValue().set(todayKey, cacheValue, 
                secondsUntilMidnight, TimeUnit.SECONDS);
            
            String verifyCache = redisTemplate.opsForValue().get(todayKey);
            if (verifyCache != null) {
                log.info("Successfully cached quote in Redis with key: {} (TTL: {} seconds, expires at midnight IST)", 
                    todayKey, secondsUntilMidnight);
            } else {
                log.error("Failed to cache quote in Redis - key: {} was not found after set operation", todayKey);
            }
        } catch (Exception e) {
            log.error("Error caching quote in Redis with key: {}", todayKey, e);
        }
        
        return savedQuote;
    }
    
    private long getSecondsUntilMidnightIST() {
        LocalDateTime now = LocalDateTime.now(IST_ZONE);
        LocalDateTime midnight = now.toLocalDate().plusDays(1).atStartOfDay();
        return ChronoUnit.SECONDS.between(now, midnight);
    }
    
    @Transactional
    public DailyQuote update(UUID id, DailyQuote quoteDetails) {
        DailyQuote quote = getById(id);
        if (quoteDetails.getText() != null) quote.setText(quoteDetails.getText());
        if (quoteDetails.getLastUsedAt() != null) quote.setLastUsedAt(quoteDetails.getLastUsedAt());
        if (quoteDetails.getIsActive() != null) quote.setIsActive(quoteDetails.getIsActive());
        return dailyQuoteRepository.save(quote);
    }
}

