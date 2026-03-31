package com.scrms.service.impl;

import com.scrms.dto.response.DashboardStats;
import com.scrms.entity.Complaint;
import com.scrms.entity.User;
import com.scrms.enums.ComplaintStatus;
import com.scrms.enums.Role;
import com.scrms.exception.ResourceNotFoundException;
import com.scrms.repository.ComplaintRepository;
import com.scrms.repository.UserRepository;
import com.scrms.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final ComplaintRepository complaintRepository;
    private final UserRepository userRepository;

    @Override
    public DashboardStats getAdminStats() {
        List<Complaint> all = complaintRepository.findAll();
        return buildStats(all);
    }

    @Override
    public DashboardStats getManagerStats() {
        List<Complaint> all = complaintRepository.findAll();
        return buildStats(all);
    }

    @Override
    public DashboardStats getAgentStats(String agentEmail) {
        User agent = userRepository.findByEmail(agentEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Agent not found"));
        List<Complaint> complaints = complaintRepository.findByAssignedAgent(agent);
        return buildStats(complaints);
    }

    @Override
    public DashboardStats getCustomerStats(String customerEmail) {
        User customer = userRepository.findByEmail(customerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        List<Complaint> complaints = complaintRepository.findByCustomer(customer);
        return buildStats(complaints);
    }

    private DashboardStats buildStats(List<Complaint> complaints) {
        Map<String, Long> byCategory = complaints.stream()
                .collect(Collectors.groupingBy(
                        c -> c.getCategory().name(), Collectors.counting()));

        Map<String, Long> byPriority = complaints.stream()
                .collect(Collectors.groupingBy(
                        c -> c.getPriority().name(), Collectors.counting()));

        Map<String, Long> byStatus = Arrays.stream(ComplaintStatus.values())
                .collect(Collectors.toMap(
                        Enum::name,
                        s -> complaints.stream().filter(c -> c.getStatus() == s).count()));

        long totalAgents = userRepository.findByRole(Role.ROLE_AGENT).size();
        long totalCustomers = userRepository.findByRole(Role.ROLE_CUSTOMER).size();

        return DashboardStats.builder()
                .totalComplaints(complaints.size())
                .newComplaints(count(complaints, ComplaintStatus.NEW))
                .inProgressComplaints(count(complaints, ComplaintStatus.IN_PROGRESS))
                .resolvedComplaints(count(complaints, ComplaintStatus.RESOLVED))
                .closedComplaints(count(complaints, ComplaintStatus.CLOSED))
                .slaBreachedComplaints(complaints.stream().filter(Complaint::isSlaBreached).count())
                .totalAgents(totalAgents)
                .totalCustomers(totalCustomers)
                .complaintsByCategory(byCategory)
                .complaintsByPriority(byPriority)
                .complaintsByStatus(byStatus)
                .build();
    }

    private long count(List<Complaint> list, ComplaintStatus status) {
        return list.stream().filter(c -> c.getStatus() == status).count();
    }
}
