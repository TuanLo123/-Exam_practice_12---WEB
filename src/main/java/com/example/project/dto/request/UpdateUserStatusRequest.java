package com.example.project.dto.request;

public class UpdateUserStatusRequest {
    private String status;
    private String lockReason;

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getLockReason() { return lockReason; }
    public void setLockReason(String lockReason) { this.lockReason = lockReason; }
}
