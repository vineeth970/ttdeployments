package com.scrms.service.impl;

import com.scrms.dto.request.AssignRequest;
import com.scrms.dto.request.ComplaintRequest;
import com.scrms.dto.request.StatusUpdateRequest;
import com.scrms.dto.response.*;
import com.scrms.entity.*;
import com.scrms.enums.ComplaintStatus;
import com.scrms.enums.Role;
import com.scrms.exception.BadRequestException;
import com.scrms.exception.ResourceNotFoundException;
import com.scrms.exception.UnauthorizedException;
import com.scrms.repository.*;
import com.scrms.service.ComplaintService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ComplaintServiceImpl implements ComplaintService {

    private final ComplaintRepository complaintRepository;
    private final UserRepository userRepository;
    private final AuditLogRepository auditLogRepository;
    private final SlaPolicyRepository slaPolicyRepository;
    private final CommentRepository commentRepository;

    // ── Create ────────────────────────────────────────────────────────────
    @Override
    @Transactional
    public ComplaintResponse createComplaint(ComplaintRequest request, String customerEmail) {
        User customer = findUserByEmail(customerEmail);

        String refNum = generateReferenceNumber();

        // Calculate SLA deadline
        Optional<SlaPolicy> policy = slaPolicyRepository.findByCategoryAndPriority(
                request.getCategory(), request.getPriority());
        LocalDateTime slaDeadline = policy
                .map(p -> LocalDateTime.now().plusHours(p.getResolutionHours()))
                .orElse(LocalDateTime.now().plusHours(72));

        Complaint complaint = Complaint.builder()
                .referenceNumber(refNum)
                .title(request.getTitle())
                .description(request.getDescription())
                .category(request.getCategory())
                .priority(request.getPriority())
                .status(ComplaintStatus.NEW)
                .customer(customer)
                .slaDeadline(slaDeadline)
                .build();

        Complaint saved = complaintRepository.save(complaint);

        logAudit(saved, customer, "COMPLAINT_CREATED", null,
                ComplaintStatus.NEW.name(), "Complaint submitted by customer");

        log.info("Complaint created: {} by {}", refNum, customerEmail);
        return mapToResponse(saved, true);
    }

    // ── Read ──────────────────────────────────────────────────────────────
    @Override
    public ComplaintResponse getComplaintById(Long id, String userEmail) {
        Complaint complaint = findComplaintById(id);
        User user = findUserByEmail(userEmail);
        validateAccess(complaint, user);
        return mapToResponse(complaint, true);
    }

    @Override
    public ComplaintResponse getComplaintByReference(String referenceNumber) {
        Complaint complaint = complaintRepository.findByReferenceNumber(referenceNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint not found: " + referenceNumber));
        return mapToResponse(complaint, false); // public tracking - no internal comments
    }

    @Override
    public List<ComplaintResponse> getAllComplaints(String userEmail) {
        User user = findUserByEmail(userEmail);
        List<Complaint> complaints;

        if (user.getRole() == Role.ROLE_ADMIN || user.getRole() == Role.ROLE_MANAGER) {
            complaints = complaintRepository.findAll();
        } else if (user.getRole() == Role.ROLE_AGENT) {
            complaints = complaintRepository.findByAssignedAgent(user);
        } else {
            complaints = complaintRepository.findByCustomer(user);
        }

        return complaints.stream()
                .map(c -> mapToResponse(c, false))
                .collect(Collectors.toList());
    }

    @Override
    public List<ComplaintResponse> getMyComplaints(String customerEmail) {
        User customer = findUserByEmail(customerEmail);
        return complaintRepository.findByCustomer(customer)
                .stream().map(c -> mapToResponse(c, false))
                .collect(Collectors.toList());
    }

    @Override
    public List<ComplaintResponse> getAgentComplaints(String agentEmail) {
        User agent = findUserByEmail(agentEmail);
        return complaintRepository.findByAssignedAgent(agent)
                .stream().map(c -> mapToResponse(c, false))
                .collect(Collectors.toList());
    }

    // ── Status Update ─────────────────────────────────────────────────────
    @Override
    @Transactional
    public ComplaintResponse updateStatus(Long id, StatusUpdateRequest request, String userEmail) {
        Complaint complaint = findComplaintById(id);
        User user = findUserByEmail(userEmail);

        // Customers can only close/confirm resolved complaints
        if (user.getRole() == Role.ROLE_CUSTOMER) {
            if (request.getStatus() != ComplaintStatus.CLOSED) {
                throw new UnauthorizedException("Customers can only close complaints");
            }
            if (complaint.getStatus() != ComplaintStatus.RESOLVED) {
                throw new BadRequestException("Complaint must be RESOLVED before closing");
            }
        }

        String fromStatus = complaint.getStatus().name();
        complaint.setStatus(request.getStatus());

        if (request.getStatus() == ComplaintStatus.RESOLVED) {
            complaint.setResolvedAt(LocalDateTime.now());
            complaint.setResolutionNote(request.getResolutionNote());
        }
        if (request.getStatus() == ComplaintStatus.CLOSED) {
            complaint.setClosedAt(LocalDateTime.now());
        }

        Complaint updated = complaintRepository.save(complaint);
        logAudit(updated, user, "STATUS_CHANGED", fromStatus,
                request.getStatus().name(), request.getRemarks());

        return mapToResponse(updated, true);
    }

    // ── Assignment ────────────────────────────────────────────────────────
    @Override
    @Transactional
    public ComplaintResponse assignComplaint(Long id, AssignRequest request, String managerEmail) {
        Complaint complaint = findComplaintById(id);
        User manager = findUserByEmail(managerEmail);
        User agent = userRepository.findById(request.getAgentId())
                .orElseThrow(() -> new ResourceNotFoundException("Agent", request.getAgentId()));

        if (agent.getRole() != Role.ROLE_AGENT) {
            throw new BadRequestException("User is not an agent");
        }

        String prevAgent = complaint.getAssignedAgent() != null
                ? complaint.getAssignedAgent().getFullName() : "Unassigned";

        complaint.setAssignedAgent(agent);
        complaint.setManager(manager);
        complaint.setStatus(ComplaintStatus.ASSIGNED);

        Complaint updated = complaintRepository.save(complaint);
        logAudit(updated, manager, "COMPLAINT_ASSIGNED", prevAgent,
                agent.getFullName(), request.getRemarks());

        return mapToResponse(updated, true);
    }

    @Override
    @Transactional
    public ComplaintResponse autoAssignComplaint(Long id, String managerEmail) {
        List<User> agents = userRepository.findAvailableAgentsSortedByLoad(Role.ROLE_AGENT);
        if (agents.isEmpty()) {
            throw new BadRequestException("No active agents available for auto-assignment");
        }
        AssignRequest req = new AssignRequest();
        req.setAgentId(agents.get(0).getId());
        req.setRemarks("Auto-assigned by system (round-robin)");
        return assignComplaint(id, req, managerEmail);
    }

    // ── Delete ────────────────────────────────────────────────────────────
    @Override
    @Transactional
    public void deleteComplaint(Long id, String adminEmail) {
        Complaint complaint = findComplaintById(id);
        complaintRepository.delete(complaint);
        log.info("Complaint {} deleted by admin {}", id, adminEmail);
    }

    // ── Helpers ───────────────────────────────────────────────────────────
    private String generateReferenceNumber() {
        long count = complaintRepository.count() + 1;
        return String.format("CMP-%d-%04d", LocalDateTime.now().getYear(), count);
    }

    private Complaint findComplaintById(Long id) {
        return complaintRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint", id));
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
    }

    private void validateAccess(Complaint complaint, User user) {
        if (user.getRole() == Role.ROLE_ADMIN || user.getRole() == Role.ROLE_MANAGER) return;
        if (user.getRole() == Role.ROLE_AGENT &&
                complaint.getAssignedAgent() != null &&
                complaint.getAssignedAgent().getId().equals(user.getId())) return;
        if (user.getRole() == Role.ROLE_CUSTOMER &&
                complaint.getCustomer().getId().equals(user.getId())) return;
        throw new UnauthorizedException("You do not have access to this complaint");
    }

    private void logAudit(Complaint complaint, User actor, String action,
                           String from, String to, String remarks) {
        AuditLog log = AuditLog.builder()
                .complaint(complaint)
                .actor(actor)
                .action(action)
                .fromValue(from)
                .toValue(to)
                .remarks(remarks)
                .build();
        auditLogRepository.save(log);
    }

    // ── Mapping ───────────────────────────────────────────────────────────
    public ComplaintResponse mapToResponse(Complaint c, boolean includeDetails) {
        ComplaintResponse.ComplaintResponseBuilder builder = ComplaintResponse.builder()
                .id(c.getId())
                .referenceNumber(c.getReferenceNumber())
                .title(c.getTitle())
                .description(c.getDescription())
                .category(c.getCategory())
                .priority(c.getPriority())
                .status(c.getStatus())
                .customer(AuthServiceImpl.mapToUserResponse(c.getCustomer()))
                .slaDeadline(c.getSlaDeadline())
                .resolvedAt(c.getResolvedAt())
                .closedAt(c.getClosedAt())
                .resolutionNote(c.getResolutionNote())
                .slaBreached(c.isSlaBreached())
                .createdAt(c.getCreatedAt())
                .updatedAt(c.getUpdatedAt());

        if (c.getAssignedAgent() != null)
            builder.assignedAgent(AuthServiceImpl.mapToUserResponse(c.getAssignedAgent()));
        if (c.getManager() != null)
            builder.manager(AuthServiceImpl.mapToUserResponse(c.getManager()));

        if (includeDetails) {
            List<CommentResponse> comments = commentRepository
                    .findByComplaintOrderByCreatedAtAsc(c)
                    .stream()
                    .map(cm -> CommentResponse.builder()
                            .id(cm.getId())
                            .content(cm.getContent())
                            .internal(cm.isInternal())
                            .author(AuthServiceImpl.mapToUserResponse(cm.getAuthor()))
                            .createdAt(cm.getCreatedAt())
                            .build())
                    .collect(Collectors.toList());
            builder.comments(comments);

            List<AuditLogResponse> logs = auditLogRepository
                    .findByComplaintOrderByCreatedAtDesc(c)
                    .stream()
                    .map(al -> AuditLogResponse.builder()
                            .id(al.getId())
                            .action(al.getAction())
                            .fromValue(al.getFromValue())
                            .toValue(al.getToValue())
                            .remarks(al.getRemarks())
                            .actor(al.getActor() != null
                                    ? AuthServiceImpl.mapToUserResponse(al.getActor()) : null)
                            .createdAt(al.getCreatedAt())
                            .build())
                    .collect(Collectors.toList());
            builder.auditLogs(logs);
        }

        return builder.build();
    }
}
