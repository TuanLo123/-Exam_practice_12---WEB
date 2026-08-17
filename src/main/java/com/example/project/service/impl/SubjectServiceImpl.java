package com.example.project.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.project.entity.Subject;
import com.example.project.repository.SubjectRepository;
import com.example.project.service.SubjectService;

@Service
public class SubjectServiceImpl implements SubjectService {
    private final SubjectRepository subjectRepository;

    public SubjectServiceImpl(SubjectRepository subjectRepository) {
        this.subjectRepository = subjectRepository;
    }

    @Override
    public Subject createSubject(Subject subject) {
        if (subject.getCode() == null || subject.getName() == null) {
            throw new RuntimeException("Tên môn và mã môn không được để trống!");
        }

        return subjectRepository.save(subject);
    }

    @Override
    public Subject updateSubject(Subject subject) {
        Subject existingSubject = subjectRepository.findById(subject.getId()).orElseThrow(
            () -> new RuntimeException("Không tim thấy môn học với ID: " + subject.getId())
        );

        if (existingSubject.getCode() == null || subject.getCode().isBlank() || subject.getName().isBlank() || existingSubject.getName() == null) {
            throw new RuntimeException("Tên môn và mã môn không được bỏ trống");
        }

        existingSubject.setName(subject.getName());
        existingSubject.setCode(subject.getCode());

        return subjectRepository.save(existingSubject);
    }

    @Override
    public Subject getSubjectById(Integer id) {
        return subjectRepository.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy môn học với ID:" + id));
    }

    @Override
    public List<Subject> getAllSubjects() {
        return subjectRepository.findAll();
    }

    @Override
    public void deleteSubject(Integer id) {
        Subject subject = subjectRepository.findById(id).orElseThrow(
            () -> new RuntimeException("Không tìm thấy môn học với ID: "+ id)
        );
        subjectRepository.delete(subject);
    }
}
