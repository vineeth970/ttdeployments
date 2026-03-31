package com.scrms.service;

import com.scrms.dto.response.DashboardStats;

public interface DashboardService {
    DashboardStats getAdminStats();
    DashboardStats getManagerStats();
    DashboardStats getAgentStats(String agentEmail);
    DashboardStats getCustomerStats(String customerEmail);
}
