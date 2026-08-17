package com.example.project.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    @Column(name = "email")
    private String email;

    @Column(name = "status")
    private String status;

    @Column(name = "warning_count")
    private int warningCount;

    @Column(name = "lock_reason", length = 500)
    private String lockReason;

    public void setId(int id) {this.id = id;}
    public void setPassword(String password) {this.password = password;}
    public void setRole(Role role) {this.role = role;}
    public void setEmail(String email) {this.email = email;}
    public void setStatus(String status) {this.status = status;}
    public void setWarningCount(int warningCount) {this.warningCount = warningCount;}
    public void setUsername(String username) {this.username = username;}
    public void setLockReason(String lockReason) {this.lockReason = lockReason;}
    
    public int getId() {return id;}
    public String getPassword() {return password;}
    public Role getRole() {return role;}
    public String getEmail() {return email;}
    public String getStatus() {return status;}
    public int getWarningCount() {return warningCount;}
    public String getUsername() {return username;}
    public String getLockReason() {return lockReason;}
}
