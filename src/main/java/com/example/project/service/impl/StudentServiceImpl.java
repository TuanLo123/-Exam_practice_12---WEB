package com.example.project.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.project.entity.Student;
import com.example.project.repository.StudentRepository;
import com.example.project.service.StudentService;

@Service
public class StudentServiceImpl implements StudentService {
    private final StudentRepository studentRepository;

    public StudentServiceImpl(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

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
        
        if (existingStudent.getAvatarUrl() == null || existingStudent.getAvatarUrl().isBlank() || 
        existingStudent.getAvatarUrl().equals(DEFAULT_MALE_AVATAR) || existingStudent.getAvatarUrl().equals(DEFAULT_FEMALE_AVATAR)) {
            existingStudent.setAvatarUrl("Nam".equals(existingStudent.getGender()) ? DEFAULT_MALE_AVATAR : DEFAULT_FEMALE_AVATAR);
        }
        
        if (student.getUsernameDisplay() == null || student.getUsernameDisplay().isBlank()) {
            throw new RuntimeException("Tên hiển thị không được để trống");
        }

        if (student.getBirthday() == null) {
            throw new RuntimeException("Ngày sinh không được để trống");
        }

        existingStudent.setUsernameDisplay(student.getUsernameDisplay());
        existingStudent.setBirthday(student.getBirthday());
        existingStudent.setGender(student.getGender());
        existingStudent.setSchoolName(student.getSchoolName());

        return studentRepository.save(existingStudent);
    }

    @Override
    public Student getStudentInfoById(Integer id) {
        return studentRepository.findById(id).orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản với ID: " + id));
    }

    @Override
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    @Override
    public void deleteStudent(Integer id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy học sinh với ID: " + id));
        studentRepository.delete(student);
    }
}