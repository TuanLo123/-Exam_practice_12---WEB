package com.example.project.service.impl;

import org.springframework.stereotype.Service;
import java.util.*;

import com.example.project.entity.Document;
import com.example.project.entity.User;
import com.example.project.repository.DocumentRepository;
import com.example.project.repository.UserRepository;
import com.example.project.service.DocumentService;

@Service
public class DocumentServiceImpl implements DocumentService{
    private final DocumentRepository documentRepository;
    private final UserRepository userRepository;

    public DocumentServiceImpl(DocumentRepository documentRepository, UserRepository userRepository) {
        this.documentRepository = documentRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Document createDocument(Document document) {

        if (document.getName() == null || document.getName().isBlank()) {
            throw new RuntimeException("Vui lòng nhập tên tài liệu");
        }

        if (document.getType() == null || document.getType().isBlank()) {
            throw new RuntimeException("Vui lòng chọn loại tài liệu");
        }

        if (document.getSubject() == null) {
            throw new RuntimeException("Vui lòng chọn môn học");
        }

        if (document.getUploadedBy() == null) {
            throw new RuntimeException("Không xác định được người tải tài liệu");
        }

        return documentRepository.save(document);
    }

    @Override
    public Document updateDocument(Document document) {

        if (document.getId() == null) {
            throw new RuntimeException("ID tài liệu không được để trống");
        }

        Document existingDocument = documentRepository.findById(document.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài liệu"));

        if (document.getName() == null || document.getName().isBlank()) {
            throw new RuntimeException("Vui lòng nhập tên tài liệu");
        }

        if (document.getType() == null || document.getType().isBlank()) {
            throw new RuntimeException("Vui lòng chọn loại tài liệu");
        }

        existingDocument.setName(document.getName());
        existingDocument.setType(document.getType());
        existingDocument.setFileUrl(document.getFileUrl());
        existingDocument.setSubject(document.getSubject());

        return documentRepository.save(existingDocument);
    }

    @Override
    public Document findDocumentById(Integer id) {

        return documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài liệu với ID: " + id));
    }

    @Override
    public List<Document> getAllDocuments() {

        return documentRepository.findAll();
    }

    @Override
    public void deleteDocument(Integer id) {

        if (!documentRepository.existsById(id)) {
            throw new RuntimeException("Không tìm thấy tài liệu với ID: " + id);
        }

        documentRepository.deleteById(id);
    }

    @Override
    public List<Document> getPendingDocuments() {
        return documentRepository.findByStatus("PENDING");
    }

    @Override
    public Document reviewDocument(Integer documentId, Integer adminId, String status) {

        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài liệu"));

        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người duyệt"));

        if (!"PENDING".equals(document.getStatus())) {
            throw new RuntimeException("Tài liệu không ở trạng thái chờ duyệt");
        }

        if (!"APPROVED".equals(status) && !"REJECTED".equals(status)) {
            throw new RuntimeException("Trạng thái không hợp lệ");
        }

        document.setStatus(status);
        document.setApprovedBy(admin);

        return documentRepository.save(document);
    }

}
