package com.example.project.entity;

import java.time.*;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "students")
@Getter
@Setter
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "username_display")
    private String usernameDisplay;

    @Column(name = "star_count")
    private int starCount;

    @Column(name = "birthday")
    private LocalDateTime birthday;

    @Column(name = "school_name")
    private String schoolName;

    @Column(name = "user_id")
    private int userId;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(name = "gender")
    private String gender;

    public Student() {

    }

    public Student(int id, String usernameDisplay, int starCount, LocalDateTime birthday, String schoolName, int userId, String avatarUrl, String gender) {
        this.id = id;
        this.usernameDisplay = usernameDisplay;
        this.starCount = starCount;
        this.birthday = birthday;
        this.schoolName = schoolName;
        this.userId = userId;
        this.avatarUrl = avatarUrl;
        this.gender = gender;
    }
}
