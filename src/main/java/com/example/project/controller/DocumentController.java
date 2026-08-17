package com.example.project.controller;

import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.example.project.security.UserPrincipal;
import com.example.project.service.DocumentService;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {
    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @GetMapping
    public ResponseEntity<?> getApprovedDocuments(Authentication authentication) {
        return ResponseEntity.ok(documentService.getApprovedDocuments(getUserId(authentication)));
    }

    @GetMapping("/my")
    public ResponseEntity<?> getMyDocuments(Authentication authentication) {
        return ResponseEntity.ok(documentService.getDocumentsByUser(getUserId(authentication)));
    }

    @PostMapping(value = "/proposals", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> proposeDocument(@RequestParam String name, @RequestParam Integer subjectId,
            @RequestParam String type, @RequestPart MultipartFile file, Authentication authentication) {
        try {
            boolean adminUpload = authentication.getAuthorities().stream()
                    .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()));

            return ResponseEntity.ok(documentService.uploadDocument(
                    name, subjectId, type, file, getUserId(authentication), adminUpload));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{id}/stars")
    public ResponseEntity<?> addStar(@PathVariable Integer id, Authentication authentication) {
        try {
            return ResponseEntity.ok(documentService.addStar(id, getUserId(authentication)));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}/stars")
    public ResponseEntity<?> removeStar(@PathVariable Integer id, Authentication authentication) {
        try {
            return ResponseEntity.ok(documentService.removeStar(id, getUserId(authentication)));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/favorites")
    public ResponseEntity<?> getFavorites(Authentication authentication) {
        return ResponseEntity.ok(documentService.getFavoriteDocuments(getUserId(authentication)));
    }

    @PostMapping("/{id}/access")
    public ResponseEntity<?> recordAccess(@PathVariable Integer id, Authentication authentication) {
        try {
            return ResponseEntity.ok(documentService.recordAccess(id, getUserId(authentication)));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/history")
    public ResponseEntity<?> getAccessHistory(Authentication authentication) {
        return ResponseEntity.ok(documentService.getAccessHistory(getUserId(authentication)));
    }

    private Integer getUserId(Authentication authentication) {
        return ((UserPrincipal) authentication.getPrincipal()).getUserId();
    }
}
