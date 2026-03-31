package com.scrms.controller;

import com.scrms.dto.response.ApiResponse;
import com.scrms.dto.response.DashboardStats;
import com.scrms.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/admin")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<DashboardStats>> adminDashboard() {
        return ResponseEntity.ok(ApiResponse.success(dashboardService.getAdminStats()));
    }

    @GetMapping("/manager")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_MANAGER')")
    public ResponseEntity<ApiResponse<DashboardStats>> managerDashboard() {
        return ResponseEntity.ok(ApiResponse.success(dashboardService.getManagerStats()));
    }

    @GetMapping("/agent")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_MANAGER','ROLE_AGENT')")
    public ResponseEntity<ApiResponse<DashboardStats>> agentDashboard(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success(
                dashboardService.getAgentStats(userDetails.getUsername())));
    }

    @GetMapping("/customer")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    public ResponseEntity<ApiResponse<DashboardStats>> customerDashboard(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success(
                dashboardService.getCustomerStats(userDetails.getUsername())));
    }
}
