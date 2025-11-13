package com.abhedyam.service.interfaces;

import com.abhedyam.model.Note;

import java.util.List;
import java.util.UUID;

public interface INoteService {
    Note create(Note note);
    Note getById(UUID id);
    List<Note> getAll();
    List<Note> getByOwnerId(UUID ownerId);
    List<Note> getByCustomerId(UUID customerId);
    Note update(UUID id, Note noteDetails);
    void delete(UUID id);
}

