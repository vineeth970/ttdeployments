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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ComplaintServiceImpl implements ComplaintService {

    private static final Logger log = LoggerFactory.getLogger(ComplaintServiceImpl.class);

    private final ComplaintRepository complaintRepository;
    private final UserRepository userRepository;
    private final AuditLogRepository auditLogRepository;
    private final SlaPolicyRepository slaPolicyRepository;
    private final CommentRepository commentRepository;

    // Manual constructor injection (replaces @RequiredArgsConstructor)
    public ComplaintServiceImpl(ComplaintRepository complaintRepository, UserRepository userRepository,
                                AuditLogRepository auditLogRepository, SlaPolicyRepository slaPolicyRepository,
                                CommentRepository commentRepository) {
        this.complaintRepository = complaintRepository;
        this.userRepository = userRepository;
        this.auditLogRepository = auditLogRepository;
        this.slaPolicyRepository = slaPolicyRepository;
        this.commentRepository = commentRepository;
    }

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

        Complaint complaint = new Complaint();
        complaint.setReferenceNumber(refNum);
        complaint.setTitle(request.getTitle());
        complaint.setDescription(request.getDescription());
        complaint.setCategory(request.getCategory());
        complaint.setPriority(request.getPriority());
        complaint.setStatus(ComplaintStatus.NEW);
        complaint.setCustomer(customer);
        complaint.setSlaDeadline(slaDeadline);

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
        AuditLog auditLog = new AuditLog();
        auditLog.setComplaint(complaint);
        auditLog.setActor(actor);
        auditLog.setAction(action);
        auditLog.setFromValue(from);
        auditLog.setToValue(to);
        auditLog.setRemarks(remarks);
        auditLogRepository.save(auditLog);
    }

    // ── Mapping ───────────────────────────────────────────────────────────
    public ComplaintResponse mapToResponse(Complaint c, boolean includeDetails) {
        ComplaintResponse response = new ComplaintResponse();
        response.setId(c.getId());
        response.setReferenceNumber(c.getReferenceNumber());
        response.setTitle(c.getTitle());
        response.setDescription(c.getDescription());
        response.setCategory(c.getCategory());
        response.setPriority(c.getPriority());
        response.setStatus(c.getStatus());
        response.setCustomer(AuthServiceImpl.mapToUserResponse(c.getCustomer()));
        response.setSlaDeadline(c.getSlaDeadline());
        response.setResolvedAt(c.getResolvedAt());
        response.setClosedAt(c.getClosedAt());
        response.setResolutionNote(c.getResolutionNote());
        response.setSlaBreached(c.isSlaBreached());
        response.setCreatedAt(c.getCreatedAt());
        response.setUpdatedAt(c.getUpdatedAt());

        if (c.getAssignedAgent() != null)
            response.setAssignedAgent(AuthServiceImpl.mapToUserResponse(c.getAssignedAgent()));
        if (c.getManager() != null)
            response.setManager(AuthServiceImpl.mapToUserResponse(c.getManager()));

        if (includeDetails) {
            List<CommentResponse> comments = commentRepository
                    .findByComplaintOrderByCreatedAtAsc(c)
                    .stream()
                    .map(cm -> {
                        CommentResponse cr = new CommentResponse();
                        cr.setId(cm.getId());
                        cr.setContent(cm.getContent());
                        cr.setInternal(cm.isInternal());
                        cr.setAuthor(AuthServiceImpl.mapToUserResponse(cm.getAuthor()));
                        cr.setCreatedAt(cm.getCreatedAt());
                        return cr;
                    })
                    .collect(Collectors.toList());
            response.setComments(comments);

            List<AuditLogResponse> logs = auditLogRepository
                    .findByComplaintOrderByCreatedAtDesc(c)
                    .stream()
                    .map(al -> {
                        AuditLogResponse alr = new AuditLogResponse();
                        alr.setId(al.getId());
                        alr.setAction(al.getAction());
                        alr.setFromValue(al.getFromValue());
                        alr.setToValue(al.getToValue());
                        alr.setRemarks(al.getRemarks());
                        alr.setActor(al.getActor() != null
                                ? AuthServiceImpl.mapToUserResponse(al.getActor()) : null);
                        alr.setCreatedAt(al.getCreatedAt());
                        return alr;
                    })
                    .collect(Collectors.toList());
            response.setAuditLogs(logs);
        }

        return response;
    }
}
