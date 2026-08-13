package com.example.project.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.project.entity.Student;
import com.example.project.repository.StudentRepository;
import com.example.project.service.StudentService;

@Service
public class StudentServiceImpl implements StudentService {
    @Autowired
    private StudentRepository studentRepository;

    private static final String DEFAULT_MALE_AVATAR = "images/default_avatar_male";
    private static final String DEFAULT_FEMALE_AVATAR = "images/default_avatar_female";
    
    @Override
    public Student createStudent(Student student) {
        if (student.getUsernameDisplay() == null || student.getUsernameDisplay().isBlank()) {
            throw new RuntimeException("Tên hiển thị không được để trống");
        }

        if (student.getBirthday() == null) {
            throw new RuntimeException("Ngày sinh không được để trống");
        }

        if (student.getAvatarUrl() == null || student.getAvatarUrl().isBlank()) {
            student.setAvatarUrl("Nam".equals(student.getGender()) ? DEFAULT_MALE_AVATAR : DEFAULT_FEMALE_AVATAR);
        }

        return studentRepository.save(student);
    }

    @Override
    public Student updateStudent(Student student) {
        Student existingStudent = studentRepository.findById(student.getId()).orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin học sinh"));

        existingStudent.setUsernameDisplay(student.getUsernameDisplay());
        existingStudent.setBirthday(student.getBirthday());
        existingStudent.setGender(student.getGender());
        existingStudent.setSchoolName(student.getSchoolName());

        if (existingStudent.getAvatarUrl() == null || existingStudent.getAvatarUrl().isBlank() || 
        existingStudent.getAvatarUrl().equals(DEFAULT_MALE_AVATAR) || existingStudent.getAvatarUrl().equals(DEFAULT_FEMALE_AVATAR)) {
            existingStudent.setAvatarUrl("Nam".equals(existingStudent.getGender()) ? DEFAULT_MALE_AVATAR : DEFAULT_FEMALE_AVATAR);
        }

        return studentRepository.save(existingStudent);
    }

    @Override
    public Student getStudentInfoById(Integer id) {
        return studentRepository.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản với ID: " + id));
    }
}