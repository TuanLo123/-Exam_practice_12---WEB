package com.example.project.service;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.stereotype.Service;

@Service
public class DocumentUploadGuard {
    private static final Duration COOLDOWN = Duration.ofSeconds(15);
    private final ConcurrentHashMap<Integer, ReentrantLock> locks = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Integer, Instant> lastUploads = new ConcurrentHashMap<>();

    public void acquire(Integer userId) {
        ReentrantLock lock = locks.computeIfAbsent(userId, id -> new ReentrantLock());

        if (!lock.tryLock()) {
            throw new RuntimeException("Một tài liệu khác của bạn đang được upload");
        }

        Instant lastUpload = lastUploads.get(userId);

        if (lastUpload != null && Duration.between(lastUpload, Instant.now()).compareTo(COOLDOWN) < 0) {
            lock.unlock();
            throw new RuntimeException("Vui lòng chờ 15 giây trước khi upload tài liệu tiếp theo");
        }
    }

    public void complete(Integer userId) {
        lastUploads.put(userId, Instant.now());
        release(userId);
    }

    public void release(Integer userId) {
        ReentrantLock lock = locks.get(userId);

        if (lock != null && lock.isHeldByCurrentThread()) {
            lock.unlock();
        }
    }
}
