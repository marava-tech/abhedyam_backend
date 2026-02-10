package com.abhedyam.service.interfaces;

import com.abhedyam.dto.DailyQuoteCreateRequest;
import com.abhedyam.model.DailyQuote;

import java.util.UUID;

public interface IDailyQuoteService {
    DailyQuote create(DailyQuoteCreateRequest request);
    DailyQuote getTodaysQuote();
    DailyQuote update(UUID id, DailyQuote quoteDetails);
}

