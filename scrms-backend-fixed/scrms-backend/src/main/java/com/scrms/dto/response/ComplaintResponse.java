package com.scrms.dto.response;

import com.scrms.enums.Category;
import com.scrms.enums.ComplaintStatus;
import com.scrms.enums.Priority;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
}
