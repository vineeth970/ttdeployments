package com.scrms.dto.response;

import com.scrms.enums.Category;
import com.scrms.enums.ComplaintStatus;
import com.scrms.enums.Priority;
import java.time.LocalDateTime;
import java.util.List;

public class ComplaintResponse {
    private Long id;
    private String referenceNumber;
    private String title;
    private String description;
    private Category category;
    private Priority priority;
    private ComplaintStatus status;

    private UserResponse customer;
    private UserResponse assignedAgent;
    private UserResponse manager;

    private LocalDateTime slaDeadline;
    private LocalDateTime resolvedAt;
    private LocalDateTime closedAt;
    private String resolutionNote;
    private boolean slaBreached;

    private List<CommentResponse> comments;
    private List<AuditLogResponse> auditLogs;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Default Constructor
    public ComplaintResponse() {}

    // All-args Constructor
    public ComplaintResponse(Long id, String referenceNumber, String title, String description, Category category, Priority priority, ComplaintStatus status, UserResponse customer, UserResponse assignedAgent, UserResponse manager, LocalDateTime slaDeadline, LocalDateTime resolvedAt, LocalDateTime closedAt, String resolutionNote, boolean slaBreached, List<CommentResponse> comments, List<AuditLogResponse> auditLogs, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.referenceNumber = referenceNumber;
        this.title = title;
        this.description = description;
        this.category = category;
        this.priority = priority;
        this.status = status;
        this.customer = customer;
        this.assignedAgent = assignedAgent;
        this.manager = manager;
        this.slaDeadline = slaDeadline;
        this.resolvedAt = resolvedAt;
        this.closedAt = closedAt;
        this.resolutionNote = resolutionNote;
        this.slaBreached = slaBreached;
        this.comments = comments;
        this.auditLogs = auditLogs;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Builder-like static method
    public static ComplaintResponseBuilder builder() {
        return new ComplaintResponseBuilder();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getReferenceNumber() { return referenceNumber; }
    public void setReferenceNumber(String referenceNumber) { this.referenceNumber = referenceNumber; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }

    public ComplaintStatus getStatus() { return status; }
    public void setStatus(ComplaintStatus status) { this.status = status; }

    public UserResponse getCustomer() { return customer; }
    public void setCustomer(UserResponse customer) { this.customer = customer; }

    public UserResponse getAssignedAgent() { return assignedAgent; }
    public void setAssignedAgent(UserResponse assignedAgent) { this.assignedAgent = assignedAgent; }

    public UserResponse getManager() { return manager; }
    public void setManager(UserResponse manager) { this.manager = manager; }

    public LocalDateTime getSlaDeadline() { return slaDeadline; }
    public void setSlaDeadline(LocalDateTime slaDeadline) { this.slaDeadline = slaDeadline; }

    public LocalDateTime getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(LocalDateTime resolvedAt) { this.resolvedAt = resolvedAt; }

    public LocalDateTime getClosedAt() { return closedAt; }
    public void setClosedAt(LocalDateTime closedAt) { this.closedAt = closedAt; }

    public String getResolutionNote() { return resolutionNote; }
    public void setResolutionNote(String resolutionNote) { this.resolutionNote = resolutionNote; }

    public boolean isSlaBreached() { return slaBreached; }
    public void setSlaBreached(boolean slaBreached) { this.slaBreached = slaBreached; }

    public List<CommentResponse> getComments() { return comments; }
    public void setComments(List<CommentResponse> comments) { this.comments = comments; }

    public List<AuditLogResponse> getAuditLogs() { return auditLogs; }
    public void setAuditLogs(List<AuditLogResponse> auditLogs) { this.auditLogs = auditLogs; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // Nested Builder Class
    public static class ComplaintResponseBuilder {
        private final ComplaintResponse instance = new ComplaintResponse();

        public ComplaintResponseBuilder id(Long id) { instance.setId(id); return this; }
        public ComplaintResponseBuilder referenceNumber(String referenceNumber) { instance.setReferenceNumber(referenceNumber); return this; }
        public ComplaintResponseBuilder title(String title) { instance.setTitle(title); return this; }
        public ComplaintResponseBuilder description(String description) { instance.setDescription(description); return this; }
        public ComplaintResponseBuilder category(Category category) { instance.setCategory(category); return this; }
        public ComplaintResponseBuilder priority(Priority priority) { instance.setPriority(priority); return this; }
        public ComplaintResponseBuilder status(ComplaintStatus status) { instance.setStatus(status); return this; }
        public ComplaintResponseBuilder customer(UserResponse customer) { instance.setCustomer(customer); return this; }
        public ComplaintResponseBuilder assignedAgent(UserResponse assignedAgent) { instance.setAssignedAgent(assignedAgent); return this; }
        public ComplaintResponseBuilder manager(UserResponse manager) { instance.setManager(manager); return this; }
        public ComplaintResponseBuilder slaDeadline(LocalDateTime slaDeadline) { instance.setSlaDeadline(slaDeadline); return this; }
        public ComplaintResponseBuilder resolvedAt(LocalDateTime resolvedAt) { instance.setResolvedAt(resolvedAt); return this; }
        public ComplaintResponseBuilder closedAt(LocalDateTime closedAt) { instance.setClosedAt(closedAt); return this; }
        public ComplaintResponseBuilder resolutionNote(String resolutionNote) { instance.setResolutionNote(resolutionNote); return this; }
        public ComplaintResponseBuilder slaBreached(boolean slaBreached) { instance.setSlaBreached(slaBreached); return this; }
        public ComplaintResponseBuilder comments(List<CommentResponse> comments) { instance.setComments(comments); return this; }
        public ComplaintResponseBuilder auditLogs(List<AuditLogResponse> auditLogs) { instance.setAuditLogs(auditLogs); return this; }
        public ComplaintResponseBuilder createdAt(LocalDateTime createdAt) { instance.setCreatedAt(createdAt); return this; }
        public ComplaintResponseBuilder updatedAt(LocalDateTime updatedAt) { instance.setUpdatedAt(updatedAt); return this; }

        public ComplaintResponse build() { return instance; }
    }
}
