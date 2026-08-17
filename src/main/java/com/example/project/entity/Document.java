package com.example.project.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "documents")
@Getter
@Setter
public class Document {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Integer id;
    
   @Column(name = "name")
   private String name;

   @ManyToOne
   @JoinColumn(name = "subject_id")
   private Subject subject;

   @ManyToOne
   @JoinColumn(name = "uploaded_by")
   private User uploadedBy;

   @ManyToOne
   @JoinColumn(name = "approved_by")
   private User approvedBy;

   @Column(name = "type")
   private String type;

   @Column(name = "file_url")
   private String fileUrl;

   @Column(name = "storage_path")
   private String storagePath;

   @Column(name = "reject_reason")
   private String rejectReason;

   @Column(name = "created_at")
   private LocalDateTime createdAt;

   @Column(name = "status")
   private String status = "PENDING";
}
