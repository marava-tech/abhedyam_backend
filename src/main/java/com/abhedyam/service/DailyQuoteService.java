package com.abhedyam.service;

import com.abhedyam.exception.ResourceNotFoundException;
import com.abhedyam.model.DailyQuote;
import com.abhedyam.repository.DailyQuoteRepository;
import com.abhedyam.service.interfaces.IDailyQuoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DailyQuoteService implements IDailyQuoteService {
    
    private final DailyQuoteRepository dailyQuoteRepository;
    
    public DailyQuote create(DailyQuote dailyQuote) {
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
    
    @Transactional
    public DailyQuote update(UUID id, DailyQuote quoteDetails) {
        DailyQuote quote = getById(id);
        if (quoteDetails.getText() != null) quote.setText(quoteDetails.getText());
        if (quoteDetails.getLastUsedAt() != null) quote.setLastUsedAt(quoteDetails.getLastUsedAt());
        if (quoteDetails.getIsActive() != null) quote.setIsActive(quoteDetails.getIsActive());
        return dailyQuoteRepository.save(quote);
    }
}

