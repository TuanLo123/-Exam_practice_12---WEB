package com.example.project.dto.request;

public class UpdateDocumentRequest {
    private String name;
    private Integer subjectId;
    private String type;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getSubjectId() { return subjectId; }
    public void setSubjectId(Integer subjectId) { this.subjectId = subjectId; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}
