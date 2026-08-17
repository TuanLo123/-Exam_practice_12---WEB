package com.example.project.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.project.entity.Otp;

public interface OtpRepository extends JpaRepository<Otp, Integer> {
    Optional<Otp> findByEmail(String email);
}
