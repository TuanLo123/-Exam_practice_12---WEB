package com.example.project.service;

import java.util.List;

import com.example.project.entity.Subject;

public interface SubjectService {
    Subject createSubject(Subject subject);
    Subject updateSubject(Subject subject);
    Subject getSubjectById(Integer id);
    List<Subject> getAllSubjects();
    void deleteSubject(Integer id);
}
