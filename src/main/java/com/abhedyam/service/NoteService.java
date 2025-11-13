package com.abhedyam.service;

import com.abhedyam.exception.ResourceNotFoundException;
import com.abhedyam.model.Note;
import com.abhedyam.repository.NoteRepository;
import com.abhedyam.service.interfaces.INoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NoteService implements INoteService {
    
    private final NoteRepository noteRepository;
    
    public Note create(Note note) {
        return noteRepository.save(note);
    }
    
    public Note getById(UUID id) {
        return noteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Note not found with id: " + id));
    }
    
    public List<Note> getAll() {
        return noteRepository.findAll();
    }
    
    public List<Note> getByOwnerId(UUID ownerId) {
        return noteRepository.findByOwnerId(ownerId);
    }
    
    public List<Note> getByCustomerId(UUID customerId) {
        return noteRepository.findByCustomerId(customerId);
    }
    
    @Transactional
    public Note update(UUID id, Note noteDetails) {
        Note note = getById(id);
        if (noteDetails.getText() != null) note.setText(noteDetails.getText());
        if (noteDetails.getStatus() != null) note.setStatus(noteDetails.getStatus());
        return noteRepository.save(note);
    }
    
    @Transactional
    public void delete(UUID id) {
        Note note = getById(id);
        note.setDeletedAt(Instant.now());
        note.setIsActive(false);
        noteRepository.save(note);
    }
}

