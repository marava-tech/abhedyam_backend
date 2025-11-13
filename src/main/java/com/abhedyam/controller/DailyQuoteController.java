package com.abhedyam.controller;

import com.abhedyam.dto.ApiResponse;
import com.abhedyam.model.DailyQuote;
import com.abhedyam.service.interfaces.IDailyQuoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/daily-quotes")
@RequiredArgsConstructor
public class DailyQuoteController {
    
    private final IDailyQuoteService dailyQuoteService;
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<DailyQuote> create(@RequestBody DailyQuote dailyQuote) {
        return ApiResponse.success(dailyQuoteService.create(dailyQuote));
    }
    
    @GetMapping("/{id}")
    public ApiResponse<DailyQuote> getById(@PathVariable UUID id) {
        return ApiResponse.success(dailyQuoteService.getById(id));
    }
    
    @GetMapping
    public ApiResponse<List<DailyQuote>> getAll() {
        return ApiResponse.success(dailyQuoteService.getAll());
    }
    
    @GetMapping("/active")
    public ApiResponse<List<DailyQuote>> getActiveQuotes() {
        return ApiResponse.success(dailyQuoteService.getActiveQuotes());
    }
    
    @PutMapping("/{id}")
    public ApiResponse<DailyQuote> update(@PathVariable UUID id, @RequestBody DailyQuote dailyQuote) {
        return ApiResponse.success(dailyQuoteService.update(id, dailyQuote));
    }
    
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResponse<Void> delete(@PathVariable UUID id) {
        dailyQuoteService.delete(id);
        return ApiResponse.success(null);
    }
}

