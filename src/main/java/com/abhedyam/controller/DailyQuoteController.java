package com.abhedyam.controller;

import com.abhedyam.dto.ApiResponse;
import com.abhedyam.dto.DailyQuoteCreateRequest;
import com.abhedyam.model.DailyQuote;
import com.abhedyam.service.interfaces.IDailyQuoteService;
import jakarta.validation.Valid;
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
    public ApiResponse<DailyQuote> create(@Valid @RequestBody DailyQuoteCreateRequest request) {
        return ApiResponse.success(dailyQuoteService.create(request));
    }
    
    @GetMapping("/{id}")
    public ApiResponse<DailyQuote> getById(@PathVariable UUID id) {
        return ApiResponse.success(dailyQuoteService.getById(id));
    }
    
    @GetMapping
    public ApiResponse<List<DailyQuote>> getAll() {
        return ApiResponse.success(dailyQuoteService.getAll());
    }
    
    @GetMapping("/today")
    public ApiResponse<DailyQuote> getTodaysQuote() {
        return ApiResponse.success(dailyQuoteService.getTodaysQuote());
    }
    
    @PutMapping("/{id}")
    public ApiResponse<DailyQuote> update(@PathVariable UUID id, @RequestBody DailyQuote dailyQuote) {
        return ApiResponse.success(dailyQuoteService.update(id, dailyQuote));
    }
}

