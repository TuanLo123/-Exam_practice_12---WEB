package com.example.project.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.project.entity.Subject;

public interface SubjectRepository extends JpaRepository<Subject, Integer> {
    Optional<Subject> findByCode(String code);
    List<Subject> findByStatus(String status);
    Optional<Subject> findByName(String name);
    boolean existsByCode(String code);
}