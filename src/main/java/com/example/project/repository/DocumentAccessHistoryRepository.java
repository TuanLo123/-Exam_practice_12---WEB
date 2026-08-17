package com.example.project.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.project.entity.DocumentAccessHistory;

public interface DocumentAccessHistoryRepository extends JpaRepository<DocumentAccessHistory, Integer> {
    List<DocumentAccessHistory> findTop50ByUserIdOrderByAccessedAtDesc(Integer userId);
}
