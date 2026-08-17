package com.example.project.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.project.entity.DocumentReviewHistory;

public interface DocumentReviewHistoryRepository extends JpaRepository<DocumentReviewHistory, Integer> {
    List<DocumentReviewHistory> findAllByOrderByReviewedAtDesc();
}
