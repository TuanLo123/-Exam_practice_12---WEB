package com.example.project.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.util.UUID;

import com.example.project.entity.Student;
import com.example.project.repository.StudentRepository;
import com.example.project.repository.StudentSubjectRepository;
import com.example.project.repository.SubjectRepository;
import com.example.project.repository.UserRepository;
import com.example.project.entity.StudentSubject;
import com.example.project.service.StudentService;
import com.example.project.service.SupabaseStorageService;

@Service
public class StudentServiceImpl implements StudentService {
    private final StudentRepository studentRepository;
    private final SupabaseStorageService storageService;
    private final StudentSubjectRepository studentSubjectRepository;
    private final SubjectRepository subjectRepository;
    private final UserRepository userRepository;

    public StudentServiceImpl(StudentRepository studentRepository, SupabaseStorageService storageService,
            StudentSubjectRepository studentSubjectRepository, SubjectRepository subjectRepository,
            UserRepository userRepository) {
        this.studentRepository = studentRepository;
        this.storageService = storageService;
        this.studentSubjectRepository = studentSubjectRepository;
        this.subjectRepository = subjectRepository;
        this.userRepository = userRepository;
    }

    private static final String DEFAULT_MALE_AVATAR = "images/default_avatar_male.jpg";
    private static final String DEFAULT_FEMALE_AVATAR = "images/default_avatar_female.jpg";
    
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

    @Override
    public boolean existsByUserId(Integer userId) {
        return studentRepository.existsByUserId(userId);
    }

    @Override
    public Optional<Student> getMyStudentInfo(Integer userId) {
        return studentRepository.findByUserId(userId);
    }

    @Override
    public Student updateAvatar(Integer userId, MultipartFile file) {
        if (file == null || file.isEmpty()) throw new RuntimeException("Vui lòng chọn ảnh đại diện");
        if (file.getSize() > 5 * 1024 * 1024) throw new RuntimeException("Ảnh đại diện không được vượt quá 5 MB");
        if (file.getContentType() == null || !file.getContentType().startsWith("image/")) {
            throw new RuntimeException("File được chọn không phải là hình ảnh");
        }

        Student student = studentRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin học sinh"));
        String originalName = file.getOriginalFilename() == null ? "avatar.jpg" : file.getOriginalFilename();
        String extension = originalName.contains(".") ? originalName.substring(originalName.lastIndexOf('.')) : ".jpg";
        String path = "avatars/" + userId + "/" + UUID.randomUUID() + extension.replaceAll("[^a-zA-Z0-9.]", "");
        String oldAvatarUrl = student.getAvatarUrl();
        String avatarUrl = storageService.upload(file, path);
        student.setAvatarUrl(avatarUrl);
        Student savedStudent = studentRepository.save(student);

        String oldStoragePath = storageService.getStoragePath(oldAvatarUrl);
        if (oldStoragePath != null) {
            try {
                storageService.delete(oldStoragePath);
            } catch (RuntimeException e) {
            }
        }

        return savedStudent;
    }

    public List<Integer> getSelectedSubjectIds(Integer userId) {
        return studentSubjectRepository.findByUser_Id(userId).stream()
                .map(item -> item.getSubject().getId()).toList();
    }

    @Transactional
    public List<Integer> updateSelectedSubjects(Integer userId, List<Integer> subjectIds) {
        if (subjectIds == null) throw new RuntimeException("Danh sách môn học không hợp lệ");
        List<Integer> uniqueIds = subjectIds.stream().distinct().toList();
        studentSubjectRepository.deleteByUser_Id(userId);
        uniqueIds.forEach(subjectId -> {
            StudentSubject item = new StudentSubject();
            item.setUser(userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản")));
            item.setSubject(subjectRepository.findById(subjectId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy môn học")));
            studentSubjectRepository.save(item);
        });
        return uniqueIds;
    }
}
