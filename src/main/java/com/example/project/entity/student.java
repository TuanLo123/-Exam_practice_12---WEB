package com.example.project.entity;
import java.util.*;
import java.time.*;

public class student {
    private int id;
    private String password;
    private String nameSchool;
    private String username;
    private LocalDateTime createdAt;
    private String role;
    private String gender;
    private String avatarUrl;
    private String email;
    private boolean isDisabled;
    private LocalDateTime lastActive;
    private int warningCount;

    public student(int id, String password, String nameSchool, String username, LocalDateTime createdAt, String role, String gender, String avatarUrl, String email, boolean isDisabled, LocalDateTime lastActive, int warningCount) {
        this.id = id;
        this.password = password;
        this.nameSchool = nameSchool;
        this.username = username;
        this.createdAt = createdAt;
        this.role = role;
        this.gender = gender;
        this.avatarUrl = avatarUrl;
        this.email = email;
        this.isDisabled = isDisabled;
        this.lastActive = lastActive;
        this.warningCount = warningCount;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasssword() {
        return password;   
    }

    public void setNameSchool(String nameSchool) {
        this.nameSchool = nameSchool;
    }

    public String getNameSchool() {
        return nameSchool;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }

    public void setGender(String gender) {
        this.gender = gender;      
    }

    public String getGender() {
        return gender;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setIsDisabled(boolean isDisabled) {
        this.isDisabled = isDisabled;
    }

    public boolean getIsDisabled() {
        return isDisabled;
    }

    public void setLastActive(LocalDateTime lastActive) {
        this.lastActive = lastActive;
    }

    public LocalDateTime getLastActive() {
        return lastActive;
    }

    public void setWarningCount(int warningCount) {
        this.warningCount = warningCount;
    }

    public int getWarningCount() {
        return warningCount;
    }
}
