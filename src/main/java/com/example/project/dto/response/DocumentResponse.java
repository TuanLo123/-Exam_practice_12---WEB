package com.example.project.dto.response;

import java.time.LocalDateTime;

public class DocumentResponse {
    private Integer id;
    private String name;
    private Integer subjectId;
    private String subjectName;
    private String uploadedByUsername;
    private String approvedByUsername;
    private String type;
    private String fileUrl;
    private String status;
    private String rejectReason;
    private LocalDateTime createdAt;
    private long starCount;
    private boolean starred;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getSubjectId() { return subjectId; }
    public void setSubjectId(Integer subjectId) { this.subjectId = subjectId; }
    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }
    public String getUploadedByUsername() { return uploadedByUsername; }
    public void setUploadedByUsername(String uploadedByUsername) { this.uploadedByUsername = uploadedByUsername; }
    public String getApprovedByUsername() { return approvedByUsername; }
    public void setApprovedByUsername(String approvedByUsername) { this.approvedByUsername = approvedByUsername; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getFileUrl() { return fileUrl; }
    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getRejectReason() { return rejectReason; }
    public void setRejectReason(String rejectReason) { this.rejectReason = rejectReason; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public long getStarCount() { return starCount; }
    public void setStarCount(long starCount) { this.starCount = starCount; }
    public boolean isStarred() { return starred; }
    public void setStarred(boolean starred) { this.starred = starred; }
}
