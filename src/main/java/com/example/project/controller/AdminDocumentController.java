package com.example.project.controller;

import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.example.project.dto.request.ReviewDocumentRequest;
import com.example.project.dto.request.UpdateDocumentRequest;
import com.example.project.security.UserPrincipal;
import com.example.project.service.DocumentService;

@RestController
@RequestMapping("/api/admin/documents")
public class AdminDocumentController {
    private final DocumentService documentService;

    public AdminDocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @GetMapping("/pending")
    public ResponseEntity<?> getPendingDocuments(Authentication authentication) {
        return ResponseEntity.ok(documentService.getPendingDocuments(getUserId(authentication)));
    }

    @GetMapping
    public ResponseEntity<?> getAllDocuments(Authentication authentication) {
        return ResponseEntity.ok(documentService.getAllDocuments(getUserId(authentication)));
    }

    @GetMapping("/history")
    public ResponseEntity<?> getReviewHistory() {
        return ResponseEntity.ok(documentService.getReviewHistory());
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createDocument(@RequestParam String name, @RequestParam Integer subjectId,
            @RequestParam String type, @RequestPart MultipartFile file, Authentication authentication) {
        try {
            return ResponseEntity.ok(documentService.uploadDocument(
                    name, subjectId, type, file, getUserId(authentication), true));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PatchMapping("/{id}/review")
    public ResponseEntity<?> reviewDocument(@PathVariable Integer id,
            @RequestBody ReviewDocumentRequest request, Authentication authentication) {
        try {
            return ResponseEntity.ok(documentService.reviewDocument(
                    id, getUserId(authentication), request.getStatus(), request.getRejectReason()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateDocument(@PathVariable Integer id, @RequestBody UpdateDocumentRequest request,
            Authentication authentication) {
        try {
            return ResponseEntity.ok(documentService.updateDocument(id, getUserId(authentication), request.getName(),
                    request.getSubjectId(), request.getType()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDocument(@PathVariable Integer id) {
        try {
            documentService.deleteDocument(id);
            return ResponseEntity.ok(Map.of("message", "Đã xóa tài liệu"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    private Integer getUserId(Authentication authentication) {
        return ((UserPrincipal) authentication.getPrincipal()).getUserId();
    }
}
