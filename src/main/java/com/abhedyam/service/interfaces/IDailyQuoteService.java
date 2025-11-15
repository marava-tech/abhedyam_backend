package com.abhedyam.service.interfaces;

import com.abhedyam.model.DailyQuote;

import java.util.List;
import java.util.UUID;

public interface IDailyQuoteService {
    DailyQuote create(DailyQuote dailyQuote);
    DailyQuote getById(UUID id);
    List<DailyQuote> getAll();
    List<DailyQuote> getActiveQuotes();
    DailyQuote update(UUID id, DailyQuote quoteDetails);
}

