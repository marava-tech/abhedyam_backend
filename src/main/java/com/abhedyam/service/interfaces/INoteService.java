package com.abhedyam.service.interfaces;

import com.abhedyam.dto.NoteCreateRequest;
import com.abhedyam.model.Note;

import java.util.List;
import java.util.UUID;

public interface INoteService {
    Note create(NoteCreateRequest request);
    List<Note> getByOwnerId(UUID ownerId);
    List<Note> getByCustomerId(UUID customerId);
}

