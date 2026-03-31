package com.scrms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStats {
    private long totalComplaints;
    private long newComplaints;
    private long inProgressComplaints;
    private long resolvedComplaints;
    private long closedComplaints;
    private long slaBreachedComplaints;
    private long totalAgents;
    private long totalCustomers;
    private Map<String, Long> complaintsByCategory;
    private Map<String, Long> complaintsByPriority;
    private Map<String, Long> complaintsByStatus;
}
