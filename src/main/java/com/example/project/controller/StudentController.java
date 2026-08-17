package com.example.project.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.project.entity.Student;
import com.example.project.security.UserPrincipal;
import com.example.project.service.StudentService;

import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/api/students")
public class StudentController {
    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @PostMapping
    public ResponseEntity<Student> createStudent(@RequestBody Student student, Authentication authentication) {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();

        Integer userId = principal.getUserId();
        student.setUserId(userId);
        Student createdStudent = studentService.createStudent(student);

        return ResponseEntity.ok(createdStudent);
    }

    @GetMapping("/{id}")
    public Student getById(@PathVariable Integer id) {
        return studentService.getStudentInfoById(id);
    }
    
    @PutMapping("/{id}")
    public Student updateStudent(@PathVariable Integer id, @RequestBody Student student) {
        student.setId(id);
        return studentService.updateStudent(student);
    }

    @GetMapping
    public List<Student> getAllStudents() {
        return studentService.getAllStudents();
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteStudent(@PathVariable Integer id) {
        studentService.deleteStudent(id);
        return ResponseEntity.ok(Map.of("messsage", "Xoá học sinh thành công!"));
    }

    @GetMapping("/check/{userId}")
    public ResponseEntity<?> checkStudent(@PathVariable Integer userId) {

        boolean exists = studentService.existsByUserId(userId);

        return ResponseEntity.ok(
            Map.of("exists", exists)
        );
    }

    @GetMapping("/me")
    public ResponseEntity<?> getMyStudentInfo(Authentication authentication) {

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();

        int userId = principal.getUserId();

        return studentService.getMyStudentInfo(userId).map(student -> ResponseEntity.ok(student))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/me/avatar")
    public ResponseEntity<?> updateMyAvatar(@RequestParam MultipartFile file, Authentication authentication) {
        try {
            UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
            return ResponseEntity.ok(studentService.updateAvatar(principal.getUserId(), file));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/me/subjects")
    public ResponseEntity<?> getMySubjects(Authentication authentication) {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        return ResponseEntity.ok(studentService.getSelectedSubjectIds(principal.getUserId()));
    }

    @PutMapping("/me/subjects")
    public ResponseEntity<?> updateMySubjects(@RequestBody List<Integer> subjectIds, Authentication authentication) {
        try {
            UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
            return ResponseEntity.ok(studentService.updateSelectedSubjects(principal.getUserId(), subjectIds));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
