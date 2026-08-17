package com.example.project.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.Comparator;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.example.project.dto.response.DocumentResponse;
import com.example.project.entity.Document;
import com.example.project.entity.DocumentStar;
import com.example.project.entity.DocumentReviewHistory;
import com.example.project.entity.DocumentAccessHistory;
import com.example.project.entity.Subject;
import com.example.project.entity.User;
import com.example.project.repository.DocumentRepository;
import com.example.project.repository.DocumentReviewHistoryRepository;
import com.example.project.repository.DocumentAccessHistoryRepository;
import com.example.project.repository.StudentSubjectRepository;
import com.example.project.repository.DocumentStarRepository;
import com.example.project.repository.SubjectRepository;
import com.example.project.repository.UserRepository;
import com.example.project.service.DocumentService;
import com.example.project.service.DocumentUploadGuard;
import com.example.project.service.SupabaseStorageService;

@Service
public class DocumentServiceImpl implements DocumentService {
    private final DocumentRepository documentRepository;
    private final DocumentStarRepository starRepository;
    private final DocumentReviewHistoryRepository reviewHistoryRepository;
    private final DocumentAccessHistoryRepository accessHistoryRepository;
    private final StudentSubjectRepository studentSubjectRepository;
    private final SubjectRepository subjectRepository;
    private final UserRepository userRepository;
    private final SupabaseStorageService storageService;
    private final DocumentUploadGuard uploadGuard;

    public DocumentServiceImpl(DocumentRepository documentRepository, DocumentStarRepository starRepository,
            DocumentReviewHistoryRepository reviewHistoryRepository,
            DocumentAccessHistoryRepository accessHistoryRepository,
            StudentSubjectRepository studentSubjectRepository,
            SubjectRepository subjectRepository, UserRepository userRepository, SupabaseStorageService storageService,
            DocumentUploadGuard uploadGuard) {
        this.documentRepository = documentRepository;
        this.starRepository = starRepository;
        this.reviewHistoryRepository = reviewHistoryRepository;
        this.accessHistoryRepository = accessHistoryRepository;
        this.studentSubjectRepository = studentSubjectRepository;
        this.subjectRepository = subjectRepository;
        this.userRepository = userRepository;
        this.storageService = storageService;
        this.uploadGuard = uploadGuard;
    }

    public List<DocumentResponse> getApprovedDocuments(Integer userId) {
        Set<Integer> selectedSubjectIds = studentSubjectRepository.findByUser_Id(userId).stream()
                .map(item -> item.getSubject().getId()).collect(Collectors.toSet());
        return documentRepository.findByStatus("APPROVED").stream()
                .sorted(Comparator
                        .comparing((Document document) -> !selectedSubjectIds.contains(document.getSubject().getId()))
                        .thenComparing((Document document) -> starRepository.countByDocument_Id(document.getId()),
                                Comparator.reverseOrder())
                        .thenComparing(Document::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .map(document -> toResponse(document, userId)).toList();
    }

    public List<DocumentResponse> getDocumentsByUser(Integer userId) {
        return documentRepository.findByUploadedBy_Id(userId).stream().map(d -> toResponse(d, userId)).toList();
    }

    public List<DocumentResponse> getPendingDocuments(Integer adminId) {
        return documentRepository.findByStatus("PENDING").stream().map(d -> toResponse(d, adminId)).toList();
    }

    public List<DocumentResponse> getAllDocuments(Integer adminId) {
        return documentRepository.findAll().stream().map(d -> toResponse(d, adminId)).toList();
    }

    public List<DocumentReviewHistory> getReviewHistory() {
        return reviewHistoryRepository.findAllByOrderByReviewedAtDesc();
    }

    @Transactional
    public DocumentResponse uploadDocument(String name, Integer subjectId, String type, MultipartFile file,
            Integer userId, boolean adminUpload) {
        if (name == null || name.isBlank()) {
            throw new RuntimeException("Tên tài liệu không được để trống");
        }

        if (subjectId == null) {
            throw new RuntimeException("Vui lòng chọn môn học");
        }

        if (type == null || type.isBlank()) {
            throw new RuntimeException("Vui lòng chọn loại tài liệu");
        }

        uploadGuard.acquire(userId);

        try {
            User uploader = findUser(userId);
            Subject subject = subjectRepository.findById(subjectId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy môn học"));
            String originalName = file.getOriginalFilename() == null ? "document" : file.getOriginalFilename();
            String safeName = originalName.replaceAll("[^a-zA-Z0-9._-]", "_");
            String path = userId + "/" + UUID.randomUUID() + "-" + safeName;
            String fileUrl = storageService.upload(file, path);

            Document document = new Document();
            document.setName(name.trim());
            document.setSubject(subject);
            document.setUploadedBy(uploader);
            document.setType(type.trim());
            document.setFileUrl(fileUrl);
            document.setStoragePath(path);
            document.setCreatedAt(LocalDateTime.now());
            document.setStatus(adminUpload ? "APPROVED" : "PENDING");
            document.setApprovedBy(adminUpload ? uploader : null);
            DocumentResponse response = toResponse(documentRepository.save(document), userId);
            uploadGuard.complete(userId);
            return response;
        } catch (RuntimeException e) {
            uploadGuard.release(userId);
            throw e;
        }
    }

    @Transactional
    public DocumentResponse reviewDocument(Integer documentId, Integer adminId, String status, String rejectReason) {
        Document document = findDocument(documentId);

        if (!"PENDING".equals(document.getStatus())) {
            throw new RuntimeException("Tài liệu không ở trạng thái chờ duyệt");
        }

        if (!"APPROVED".equals(status) && !"REJECTED".equals(status)) {
            throw new RuntimeException("Trạng thái duyệt không hợp lệ");
        }

        if ("REJECTED".equals(status) && (rejectReason == null || rejectReason.isBlank())) {
            throw new RuntimeException("Vui lòng nhập lý do từ chối");
        }

        User reviewer = findUser(adminId);
        document.setStatus(status);
        document.setApprovedBy(reviewer);
        document.setRejectReason("REJECTED".equals(status) ? rejectReason.trim() : null);
        Document savedDocument = documentRepository.save(document);

        DocumentReviewHistory history = new DocumentReviewHistory();
        history.setDocumentId(savedDocument.getId());
        history.setDocumentName(savedDocument.getName());
        history.setSubjectName(savedDocument.getSubject().getName());
        history.setUploadedByUsername(savedDocument.getUploadedBy().getUsername());
        history.setReviewedByUsername(reviewer.getUsername());
        history.setAction(status);
        history.setRejectReason("REJECTED".equals(status) ? rejectReason.trim() : null);
        history.setReviewedAt(LocalDateTime.now());
        reviewHistoryRepository.save(history);
        return toResponse(savedDocument, adminId);
    }

    public DocumentResponse updateDocument(Integer documentId, Integer adminId, String name, Integer subjectId,
            String type) {
        if (name == null || name.isBlank()) throw new RuntimeException("Tên tài liệu không được để trống");
        if (subjectId == null) throw new RuntimeException("Vui lòng chọn môn học");
        if (type == null || type.isBlank()) throw new RuntimeException("Vui lòng chọn loại tài liệu");

        Document document = findDocument(documentId);
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy môn học"));
        document.setName(name.trim());
        document.setSubject(subject);
        document.setType(type.trim());
        return toResponse(documentRepository.save(document), adminId);
    }

    @Transactional
    public void deleteDocument(Integer documentId) {
        Document document = findDocument(documentId);
        storageService.delete(document.getStoragePath());
        starRepository.deleteByDocument_Id(documentId);
        documentRepository.delete(document);
    }

    @Transactional
    public DocumentResponse addStar(Integer documentId, Integer userId) {
        Document document = findDocument(documentId);

        if (!"APPROVED".equals(document.getStatus())) {
            throw new RuntimeException("Chỉ có thể star tài liệu đã được duyệt");
        }

        if (!starRepository.existsByDocument_IdAndUser_Id(documentId, userId)) {
            DocumentStar star = new DocumentStar();
            star.setDocument(document);
            star.setUser(findUser(userId));
            star.setCreatedAt(LocalDateTime.now());
            starRepository.save(star);
        }
        return toResponse(document, userId);
    }

    @Transactional
    public DocumentResponse removeStar(Integer documentId, Integer userId) {
        Document document = findDocument(documentId);
        starRepository.findByDocument_IdAndUser_Id(documentId, userId).ifPresent(starRepository::delete);
        return toResponse(document, userId);
    }

    public List<DocumentResponse> getFavoriteDocuments(Integer userId) {
        return starRepository.findByUser_IdOrderByCreatedAtDesc(userId).stream()
                .map(star -> toResponse(star.getDocument(), userId)).toList();
    }

    public DocumentResponse recordAccess(Integer documentId, Integer userId) {
        Document document = findDocument(documentId);
        if (!"APPROVED".equals(document.getStatus())) throw new RuntimeException("Tài liệu chưa được duyệt");
        DocumentAccessHistory history = new DocumentAccessHistory();
        history.setUserId(userId);
        history.setDocumentId(document.getId());
        history.setDocumentName(document.getName());
        history.setSubjectName(document.getSubject().getName());
        history.setFileUrl(document.getFileUrl());
        history.setAccessedAt(LocalDateTime.now());
        accessHistoryRepository.save(history);
        return toResponse(document, userId);
    }

    public List<DocumentAccessHistory> getAccessHistory(Integer userId) {
        return accessHistoryRepository.findTop50ByUserIdOrderByAccessedAtDesc(userId);
    }

    private Document findDocument(Integer id) {
        return documentRepository.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy tài liệu"));
    }

    private User findUser(Integer id) {
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản"));
    }

    private DocumentResponse toResponse(Document document, Integer userId) {
        DocumentResponse response = new DocumentResponse();
        response.setId(document.getId());
        response.setName(document.getName());
        response.setSubjectId(document.getSubject().getId());
        response.setSubjectName(document.getSubject().getName());
        response.setUploadedByUsername(document.getUploadedBy().getUsername());
        response.setApprovedByUsername(document.getApprovedBy() == null ? null : document.getApprovedBy().getUsername());
        response.setType(document.getType());
        response.setFileUrl(document.getFileUrl());
        response.setStatus(document.getStatus());
        response.setRejectReason(document.getRejectReason());
        response.setCreatedAt(document.getCreatedAt());
        response.setStarCount(starRepository.countByDocument_Id(document.getId()));
        response.setStarred(userId != null && starRepository.existsByDocument_IdAndUser_Id(document.getId(), userId));
        return response;
    }
}
