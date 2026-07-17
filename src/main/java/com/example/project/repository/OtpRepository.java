package com.example.project.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.project.entity.otp;

public interface OtpRepository extends JpaRepository<otp, Integer> {
    Optional<otp> findByEmail(String email);
}
