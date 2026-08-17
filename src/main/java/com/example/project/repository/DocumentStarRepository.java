package com.example.project.repository;

import java.util.Optional;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.project.entity.DocumentStar;

public interface DocumentStarRepository extends JpaRepository<DocumentStar, Integer> {
    Optional<DocumentStar> findByDocument_IdAndUser_Id(Integer documentId, Integer userId);
    long countByDocument_Id(Integer documentId);
    boolean existsByDocument_IdAndUser_Id(Integer documentId, Integer userId);
    void deleteByDocument_Id(Integer documentId);
    List<DocumentStar> findByUser_IdOrderByCreatedAtDesc(Integer userId);
}
