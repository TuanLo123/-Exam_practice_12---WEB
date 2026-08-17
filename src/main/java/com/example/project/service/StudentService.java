package com.example.project.service;

import java.util.List;
import java.util.Optional;

import com.example.project.entity.Student;
import org.springframework.web.multipart.MultipartFile;

public interface StudentService {
    Student createStudent(Student student);
    Student updateStudent(Student student);
    Student getStudentInfoById(Integer id);
    List<Student> getAllStudents();
    void deleteStudent(Integer id);
    boolean existsByUserId(Integer userId);
    Optional<Student> getMyStudentInfo(Integer userId);
    Student updateAvatar(Integer userId, MultipartFile file);
    List<Integer> getSelectedSubjectIds(Integer userId);
    List<Integer> updateSelectedSubjects(Integer userId, List<Integer> subjectIds);
}
