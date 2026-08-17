package com.example.project.service;

import java.util.*;
import com.example.project.entity.Document;

public interface DocumentService {
    Document createDocument(Document document);
    Document updateDocument(Document document);
    Document findDocumentById(Integer Id);
    List<Document> getAllDocuments();
    void deleteDocument(Integer id);

    /// DOCUMENT PROPOSAL
    List<Document> getPendingDocuments();
    Document reviewDocument(Integer documentId, Integer adminId, String status); 
}
