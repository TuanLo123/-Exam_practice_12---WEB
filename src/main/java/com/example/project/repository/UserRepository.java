package com.example.project.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.project.entity.user;

public interface UserRepository extends JpaRepository<user, Integer> {
    Optional<user> findByUsername(String username);
}