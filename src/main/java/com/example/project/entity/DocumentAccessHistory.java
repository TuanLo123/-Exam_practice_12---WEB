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
@Table(name = "document_access_history")
@Getter
@Setter
public class DocumentAccessHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "document_id")
    private Integer documentId;

    @Column(name = "document_name", nullable = false)
    private String documentName;

    @Column(name = "subject_name", nullable = false)
    private String subjectName;

    @Column(name = "file_url", nullable = false, length = 1000)
    private String fileUrl;

    @Column(name = "accessed_at", nullable = false)
    private LocalDateTime accessedAt;
}
