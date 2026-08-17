package com.example.project.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.project.entity.StudentSubject;

public interface StudentSubjectRepository extends JpaRepository<StudentSubject, Integer> {
    List<StudentSubject> findByUser_Id(Integer userId);
    void deleteByUser_Id(Integer userId);
}
