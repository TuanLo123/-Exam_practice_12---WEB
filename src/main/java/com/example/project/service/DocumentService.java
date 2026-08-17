package com.example.project.service;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import com.example.project.dto.response.DocumentResponse;
import com.example.project.entity.DocumentReviewHistory;
import com.example.project.entity.DocumentAccessHistory;

public interface DocumentService {
    List<DocumentResponse> getApprovedDocuments(Integer userId);
    List<DocumentResponse> getDocumentsByUser(Integer userId);
    List<DocumentResponse> getPendingDocuments(Integer adminId);
    List<DocumentResponse> getAllDocuments(Integer adminId);
    List<DocumentReviewHistory> getReviewHistory();
    DocumentResponse uploadDocument(String name, Integer subjectId, String type, MultipartFile file,
            Integer userId, boolean adminUpload);
    DocumentResponse reviewDocument(Integer documentId, Integer adminId, String status, String rejectReason);
    DocumentResponse updateDocument(Integer documentId, Integer adminId, String name, Integer subjectId, String type);
    void deleteDocument(Integer documentId);
    DocumentResponse addStar(Integer documentId, Integer userId);
    DocumentResponse removeStar(Integer documentId, Integer userId);
    List<DocumentResponse> getFavoriteDocuments(Integer userId);
    DocumentResponse recordAccess(Integer documentId, Integer userId);
    List<DocumentAccessHistory> getAccessHistory(Integer userId);
}
