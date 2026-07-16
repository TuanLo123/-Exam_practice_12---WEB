package com.example.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.project.entity.role;

public interface RoleRepository extends JpaRepository<role, Integer> {
    
}
