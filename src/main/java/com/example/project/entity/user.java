package com.example.project.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class user {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "role_id")
    private int roleId;

    @Column(name = "email")
    private String email;

    @Column(name = "status")
    private String status;

    @Column(name = "warning_count")
    private int warningCount;

    public void setId(int id) {this.id = id;}
    public void setPassword(String password) {this.password = password;}
    public void setRoleId(int roleId) {this.roleId = roleId;}
    public void setEmail(String email) {this.email = email;}
    public void setStatus(String status) {this.status = status;}
    public void setWarningCount(int warningCount) {this.warningCount = warningCount;}
    
    public int getId() {return id;}
    public String getPassword() {return password;}
    public int getRoleId() {return roleId;}
    public String getEmail() {return email;}
    public String getStatus() {return status;}
    public int getWarningCount() {return warningCount;}
}
