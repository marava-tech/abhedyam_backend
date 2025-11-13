package com.abhedyam.service.interfaces;

import com.abhedyam.model.Document;

import java.util.List;
import java.util.UUID;

public interface IDocumentService {
    Document create(Document document);
    Document getById(UUID id);
    List<Document> getAll();
    List<Document> getByOwnerId(UUID ownerId);
    Document update(UUID id, Document documentDetails);
    void delete(UUID id);
}

