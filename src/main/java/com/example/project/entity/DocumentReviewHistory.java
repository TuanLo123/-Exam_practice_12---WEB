package com.example.project.entity;

import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "document_review_history")
@Getter
@Setter
public class DocumentReviewHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "document_id")
    private Integer documentId;

    @Column(name = "document_name", nullable = false)
    private String documentName;

    @Column(name = "subject_name", nullable = false)
    private String subjectName;

    @Column(name = "uploaded_by_username", nullable = false)
    private String uploadedByUsername;

    @Column(name = "reviewed_by_username", nullable = false)
    private String reviewedByUsername;

    @Column(name = "action", nullable = false)
    private String action;

    @Column(name = "reject_reason")
    private String rejectReason;

    @Column(name = "reviewed_at", nullable = false)
    private LocalDateTime reviewedAt;
}
