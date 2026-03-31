package com.scrms.dto.response;

import java.time.LocalDateTime;

public class AuditLogResponse {
    private Long id;
    private String action;
    private String fromValue;
    private String toValue;
    private String remarks;
    private UserResponse actor;
    private LocalDateTime createdAt;

    // Default Constructor
    public AuditLogResponse() {}

    // All-args Constructor
    public AuditLogResponse(Long id, String action, String fromValue, String toValue, String remarks, UserResponse actor, LocalDateTime createdAt) {
        this.id = id;
        this.action = action;
        this.fromValue = fromValue;
        this.toValue = toValue;
        this.remarks = remarks;
        this.actor = actor;
        this.createdAt = createdAt;
    }

    // Builder factory method
    public static AuditLogResponseBuilder builder() {
        return new AuditLogResponseBuilder();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getFromValue() { return fromValue; }
    public void setFromValue(String fromValue) { this.fromValue = fromValue; }

    public String getToValue() { return toValue; }
    public void setToValue(String toValue) { this.toValue = toValue; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public UserResponse getActor() { return actor; }
    public void setActor(UserResponse actor) { this.actor = actor; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    // Nested Builder Class
    public static class AuditLogResponseBuilder {
        private final AuditLogResponse instance = new AuditLogResponse();

        public AuditLogResponseBuilder id(Long id) { instance.setId(id); return this; }
        public AuditLogResponseBuilder action(String action) { instance.setAction(action); return this; }
        public AuditLogResponseBuilder fromValue(String fromValue) { instance.setFromValue(fromValue); return this; }
        public AuditLogResponseBuilder toValue(String toValue) { instance.setToValue(toValue); return this; }
        public AuditLogResponseBuilder remarks(String remarks) { instance.setRemarks(remarks); return this; }
        public AuditLogResponseBuilder actor(UserResponse actor) { instance.setActor(actor); return this; }
        public AuditLogResponseBuilder createdAt(LocalDateTime createdAt) { instance.setCreatedAt(createdAt); return this; }

        public AuditLogResponse build() { return instance; }
    }
}
