package com.abhedyam.controller;

import com.abhedyam.dto.ApiResponse;
import com.abhedyam.model.Note;
import com.abhedyam.service.interfaces.INoteService;
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
    public ApiResponse<Note> create(@RequestBody Note note) {
        return ApiResponse.success(noteService.create(note));
    }
    
    @GetMapping("/{id}")
    public ApiResponse<Note> getById(@PathVariable UUID id) {
        return ApiResponse.success(noteService.getById(id));
    }
    
    @GetMapping
    public ApiResponse<List<Note>> getAll() {
        return ApiResponse.success(noteService.getAll());
    }
    
    @GetMapping("/owner/{ownerId}")
    public ApiResponse<List<Note>> getByOwnerId(@PathVariable UUID ownerId) {
        return ApiResponse.success(noteService.getByOwnerId(ownerId));
    }
    
    @GetMapping("/customer/{customerId}")
    public ApiResponse<List<Note>> getByCustomerId(@PathVariable UUID customerId) {
        return ApiResponse.success(noteService.getByCustomerId(customerId));
    }
    
    @PutMapping("/{id}")
    public ApiResponse<Note> update(@PathVariable UUID id, @RequestBody Note note) {
        return ApiResponse.success(noteService.update(id, note));
    }
    
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResponse<Void> delete(@PathVariable UUID id) {
        noteService.delete(id);
        return ApiResponse.success(null);
    }
}

