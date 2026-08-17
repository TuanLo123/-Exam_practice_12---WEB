package com.example.project.service;

import java.util.List;

import com.example.project.entity.Student;

public interface StudentService {
    Student createStudent(Student student);
    Student updateStudent(Student student);
    Student getStudentInfoById(Integer id);
    List<Student> getAllStudents();
    void deleteStudent(Integer id);
}
