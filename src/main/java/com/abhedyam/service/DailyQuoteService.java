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
import java.time.LocalDateTime;
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
    
    private static final String TODAY_QUOTE_KEY = "daily-quote:today";
    
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
        String cachedQuoteId = redisTemplate.opsForValue().get(TODAY_QUOTE_KEY);
        
        if (cachedQuoteId != null) {
            try {
                UUID quoteId = UUID.fromString(cachedQuoteId);
                DailyQuote quote = dailyQuoteRepository.findById(quoteId)
                    .orElse(null);
                
                if (quote != null && quote.getIsActive()) {
                    quote.setLastUsedAt(Instant.now());
                    dailyQuoteRepository.save(quote);
                    log.info("Returning cached today's quote: {}", quoteId);
                    return quote;
                }
            } catch (Exception e) {
                log.warn("Error retrieving cached quote, selecting new one", e);
            }
        }
        
        List<DailyQuote> activeQuotes = dailyQuoteRepository.findActiveQuotesOrderByLastUsedAsc();
        
        if (activeQuotes.isEmpty()) {
            throw new ResourceNotFoundException("No active quotes available");
        }
        
        DailyQuote selectedQuote = activeQuotes.get(0);
        selectedQuote.setLastUsedAt(Instant.now());
        DailyQuote savedQuote = dailyQuoteRepository.save(selectedQuote);
        
        long secondsUntilMidnight = getSecondsUntilMidnight();
        redisTemplate.opsForValue().set(TODAY_QUOTE_KEY, savedQuote.getId().toString(), 
            secondsUntilMidnight, TimeUnit.SECONDS);
        
        log.info("Selected new quote for today: {} (ID: {})", 
            savedQuote.getText().substring(0, Math.min(50, savedQuote.getText().length())), 
            savedQuote.getId());
        
        return savedQuote;
    }
    
    private long getSecondsUntilMidnight() {
        LocalDateTime now = LocalDateTime.now();
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

