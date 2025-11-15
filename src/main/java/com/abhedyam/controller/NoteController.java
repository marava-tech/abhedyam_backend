package com.abhedyam.controller;

import com.abhedyam.dto.ApiResponse;
import com.abhedyam.dto.NoteCreateRequest;
import com.abhedyam.model.Note;
import com.abhedyam.service.interfaces.INoteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notes")
@RequiredArgsConstructor
public class NoteController {
    
    private final INoteService noteService;
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<Note> create(@Valid @RequestBody NoteCreateRequest request) {
        return ApiResponse.success(noteService.create(request));
    }
    
    @GetMapping("/{id}")
    public ApiResponse<Note> getById(@PathVariable UUID id) {
        return ApiResponse.success(noteService.getById(id));
    }
    
    @GetMapping("/customer/{customerId}")
    public ApiResponse<List<Note>> getByCustomerId(@PathVariable UUID customerId) {
        return ApiResponse.success(noteService.getByCustomerId(customerId));
    }
    
    @PutMapping("/{id}")
    public ApiResponse<Note> update(@PathVariable UUID id, @RequestParam String text) {
        return ApiResponse.success(noteService.update(id, text));
    }
}

