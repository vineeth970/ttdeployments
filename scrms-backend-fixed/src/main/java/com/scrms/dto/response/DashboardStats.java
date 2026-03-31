package com.scrms.dto.response;

import java.util.Map;

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

    // Default Constructor
    public DashboardStats() {}

    // All-args Constructor
    public DashboardStats(long totalComplaints, long newComplaints, long inProgressStatus, long resolvedComplaints, long closedComplaints, long slaBreachedComplaints, long totalAgents, long totalCustomers, Map<String, Long> complaintsByCategory, Map<String, Long> complaintsByPriority, Map<String, Long> complaintsByStatus) {
        this.totalComplaints = totalComplaints;
        this.newComplaints = newComplaints;
        this.inProgressComplaints = inProgressStatus;
        this.resolvedComplaints = resolvedComplaints;
        this.closedComplaints = closedComplaints;
        this.slaBreachedComplaints = slaBreachedComplaints;
        this.totalAgents = totalAgents;
        this.totalCustomers = totalCustomers;
        this.complaintsByCategory = complaintsByCategory;
        this.complaintsByPriority = complaintsByPriority;
        this.complaintsByStatus = complaintsByStatus;
    }

    // Builder-like static method (simulating what was there before)
    public static DashboardStatsBuilder builder() {
        return new DashboardStatsBuilder();
    }

    // Getters and Setters
    public long getTotalComplaints() { return totalComplaints; }
    public void setTotalComplaints(long totalComplaints) { this.totalComplaints = totalComplaints; }

    public long getNewComplaints() { return newComplaints; }
    public void setNewComplaints(long newComplaints) { this.newComplaints = newComplaints; }

    public long getInProgressComplaints() { return inProgressComplaints; }
    public void setInProgressComplaints(long inProgressComplaints) { this.inProgressComplaints = inProgressComplaints; }

    public long getResolvedComplaints() { return resolvedComplaints; }
    public void setResolvedComplaints(long resolvedComplaints) { this.resolvedComplaints = resolvedComplaints; }

    public long getClosedComplaints() { return closedComplaints; }
    public void setClosedComplaints(long closedComplaints) { this.closedComplaints = closedComplaints; }

    public long getSlaBreachedComplaints() { return slaBreachedComplaints; }
    public void setSlaBreachedComplaints(long slaBreachedComplaints) { this.slaBreachedComplaints = slaBreachedComplaints; }

    public long getTotalAgents() { return totalAgents; }
    public void setTotalAgents(long totalAgents) { this.totalAgents = totalAgents; }

    public long getTotalCustomers() { return totalCustomers; }
    public void setTotalCustomers(long totalCustomers) { this.totalCustomers = totalCustomers; }

    public Map<String, Long> getComplaintsByCategory() { return complaintsByCategory; }
    public void setComplaintsByCategory(Map<String, Long> complaintsByCategory) { this.complaintsByCategory = complaintsByCategory; }

    public Map<String, Long> getComplaintsByPriority() { return complaintsByPriority; }
    public void setComplaintsByPriority(Map<String, Long> complaintsByPriority) { this.complaintsByPriority = complaintsByPriority; }

    public Map<String, Long> getComplaintsByStatus() { return complaintsByStatus; }
    public void setComplaintsByStatus(Map<String, Long> complaintsByStatus) { this.complaintsByStatus = complaintsByStatus; }

    // Nested Builder Class
    public static class DashboardStatsBuilder {
        private final DashboardStats stats = new DashboardStats();

        public DashboardStatsBuilder totalComplaints(long totalComplaints) { stats.setTotalComplaints(totalComplaints); return this; }
        public DashboardStatsBuilder newComplaints(long newComplaints) { stats.setNewComplaints(newComplaints); return this; }
        public DashboardStatsBuilder inProgressComplaints(long inProgressComplaints) { stats.setInProgressComplaints(inProgressComplaints); return this; }
        public DashboardStatsBuilder resolvedComplaints(long resolvedComplaints) { stats.setResolvedComplaints(resolvedComplaints); return this; }
        public DashboardStatsBuilder closedComplaints(long closedComplaints) { stats.setClosedComplaints(closedComplaints); return this; }
        public DashboardStatsBuilder slaBreachedComplaints(long slaBreachedComplaints) { stats.setSlaBreachedComplaints(slaBreachedComplaints); return this; }
        public DashboardStatsBuilder totalAgents(long totalAgents) { stats.setTotalAgents(totalAgents); return this; }
        public DashboardStatsBuilder totalCustomers(long totalCustomers) { stats.setTotalCustomers(totalCustomers); return this; }
        public DashboardStatsBuilder complaintsByCategory(Map<String, Long> complaintsByCategory) { stats.setComplaintsByCategory(complaintsByCategory); return this; }
        public DashboardStatsBuilder complaintsByPriority(Map<String, Long> complaintsByPriority) { stats.setComplaintsByPriority(complaintsByPriority); return this; }
        public DashboardStatsBuilder complaintsByStatus(Map<String, Long> complaintsByStatus) { stats.setComplaintsByStatus(complaintsByStatus); return this; }

        public DashboardStats build() { return stats; }
    }
}
