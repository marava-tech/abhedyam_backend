package com.abhedyam.service;

import com.abhedyam.dto.NoteCreateRequest;
import com.abhedyam.exception.BusinessException;
import com.abhedyam.exception.ResourceNotFoundException;
import com.abhedyam.model.Note;
import com.abhedyam.model.enums.NoteStatus;
import com.abhedyam.repository.CustomerRepository;
import com.abhedyam.repository.NoteRepository;
import com.abhedyam.service.interfaces.INoteService;
import com.abhedyam.util.SecurityUtil;
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
    private final CustomerRepository customerRepository;
    
    @Override
    @Transactional
    public Note create(NoteCreateRequest request) {
        UUID ownerId = SecurityUtil.getCurrentUserId();
        
        if (!customerRepository.existsById(request.getCustomerId())) {
            throw new ResourceNotFoundException("Customer not found");
        }
        
        Note note = new Note();
        note.setCustomerId(request.getCustomerId());
        note.setOwnerId(ownerId);
        note.setText(request.getText());
        note.setStatus(NoteStatus.PENDING);
        
        return noteRepository.save(note);
    }
    
    @Override
    public Note getById(UUID id) {
        UUID ownerId = SecurityUtil.getCurrentUserId();
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Note not found with id: " + id));
        
        if (!note.getOwnerId().equals(ownerId)) {
            throw new BusinessException("UNAUTHORIZED", "You don't have access to this note");
        }
        
        return note;
    }
    
    @Override
    public List<Note> getAll() {
        return noteRepository.findAll();
    }
    
    @Override
    public List<Note> getByOwnerId(UUID ownerId) {
        UUID currentOwnerId = SecurityUtil.getCurrentUserId();
        if (!currentOwnerId.equals(ownerId)) {
            throw new BusinessException("UNAUTHORIZED", "You can only view your own notes");
        }
        return noteRepository.findByOwnerId(ownerId);
    }
    
    @Override
    public List<Note> getByCustomerId(UUID customerId) {
        UUID ownerId = SecurityUtil.getCurrentUserId();
        List<Note> notes = noteRepository.findByCustomerId(customerId);
        return notes.stream()
            .filter(note -> note.getOwnerId().equals(ownerId))
            .toList();
    }
    
    @Override
    @Transactional
    public Note update(UUID id, String text) {
        Note note = getById(id);
        note.setText(text);
        return noteRepository.save(note);
    }
    
    @Override
    @Transactional
    public Note updateStatus(UUID id, NoteStatus status) {
        Note note = getById(id);
        note.setStatus(status);
        return noteRepository.save(note);
    }
    
    @Override
    @Transactional
    public void delete(UUID id) {
        Note note = getById(id);
        note.setDeletedAt(Instant.now());
        note.setIsActive(false);
        noteRepository.save(note);
    }
}

