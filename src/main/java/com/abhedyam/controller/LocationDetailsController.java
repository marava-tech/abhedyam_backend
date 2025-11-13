package com.abhedyam.controller;

import com.abhedyam.dto.ApiResponse;
import com.abhedyam.model.LocationDetails;
import com.abhedyam.service.interfaces.ILocationDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/location-details")
@RequiredArgsConstructor
public class LocationDetailsController {
    
    private final ILocationDetailsService locationDetailsService;
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<LocationDetails> create(@RequestBody LocationDetails locationDetails) {
        return ApiResponse.success(locationDetailsService.create(locationDetails));
    }
    
    @GetMapping("/{id}")
    public ApiResponse<LocationDetails> getById(@PathVariable UUID id) {
        return ApiResponse.success(locationDetailsService.getById(id));
    }
    
    @GetMapping
    public ApiResponse<List<LocationDetails>> getAll() {
        return ApiResponse.success(locationDetailsService.getAll());
    }
    
    @PutMapping("/{id}")
    public ApiResponse<LocationDetails> update(@PathVariable UUID id, @RequestBody LocationDetails locationDetails) {
        return ApiResponse.success(locationDetailsService.update(id, locationDetails));
    }
    
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResponse<Void> delete(@PathVariable UUID id) {
        locationDetailsService.delete(id);
        return ApiResponse.success(null);
    }
}

