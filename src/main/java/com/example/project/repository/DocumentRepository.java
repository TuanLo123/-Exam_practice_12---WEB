package com.example.project.repository;

import java.util.*;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.project.entity.Document;

public interface DocumentRepository extends JpaRepository<Document, Integer>{
    Optional<Document> findByName(String name);
    List<Document> findBySubject_Id(Integer subjectId);
    List<Document> findByUploadedBy_Id(Integer userId);
    List<Document> findByApprovedBy_Id(Integer userId);
    List<Document> findByType(String type);
    List<Document> findByStatus(String status);
}
