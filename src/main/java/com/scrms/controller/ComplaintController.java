package com.scrms.controller;

import com.scrms.dto.request.AssignRequest;
import com.scrms.dto.request.ComplaintRequest;
import com.scrms.dto.request.StatusUpdateRequest;
import com.scrms.dto.response.ApiResponse;
import com.scrms.dto.response.ComplaintResponse;
import com.scrms.service.ComplaintService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/complaints")
public class ComplaintController {

    private final ComplaintService complaintService;

    // Manual constructor injection
    public ComplaintController(ComplaintService complaintService) {
        this.complaintService = complaintService;
    }

    // ── Submit a new complaint (Customer) ────────────────────────────────
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    public ResponseEntity<ApiResponse<ComplaintResponse>> createComplaint(
            @Valid @RequestBody ComplaintRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        ComplaintResponse response = complaintService.createComplaint(request, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Complaint submitted successfully", response));
    }

    // ── Get all complaints (Admin/Manager = all, Agent = assigned, Customer = own) ──
    @GetMapping
    public ResponseEntity<ApiResponse<List<ComplaintResponse>>> getAllComplaints(
            @AuthenticationPrincipal UserDetails userDetails) {
        List<ComplaintResponse> list = complaintService.getAllComplaints(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    // ── Get single complaint by ID ───────────────────────────────────────
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ComplaintResponse>> getComplaintById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        ComplaintResponse response = complaintService.getComplaintById(id, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // ── Public tracking by reference number ─────────────────────────────
    @GetMapping("/track/{referenceNumber}")
    public ResponseEntity<ApiResponse<ComplaintResponse>> trackComplaint(
            @PathVariable String referenceNumber) {
        ComplaintResponse response = complaintService.getComplaintByReference(referenceNumber);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // ── My complaints (Customer) ─────────────────────────────────────────
    @GetMapping("/my")
    @PreAuthorize("hasAuthority('ROLE_CUSTOMER')")
    public ResponseEntity<ApiResponse<List<ComplaintResponse>>> getMyComplaints(
            @AuthenticationPrincipal UserDetails userDetails) {
        List<ComplaintResponse> list = complaintService.getMyComplaints(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    // ── Agent's assigned complaints ──────────────────────────────────────
    @GetMapping("/assigned")
    @PreAuthorize("hasAuthority('ROLE_AGENT')")
    public ResponseEntity<ApiResponse<List<ComplaintResponse>>> getAgentComplaints(
            @AuthenticationPrincipal UserDetails userDetails) {
        List<ComplaintResponse> list = complaintService.getAgentComplaints(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    // ── Update complaint status ──────────────────────────────────────────
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_MANAGER','ROLE_AGENT','ROLE_CUSTOMER')")
    public ResponseEntity<ApiResponse<ComplaintResponse>> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody StatusUpdateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        ComplaintResponse response = complaintService.updateStatus(id, request, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Status updated", response));
    }

    // ── Manually assign complaint ────────────────────────────────────────
    @PatchMapping("/{id}/assign")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_MANAGER')")
    public ResponseEntity<ApiResponse<ComplaintResponse>> assignComplaint(
            @PathVariable Long id,
            @Valid @RequestBody AssignRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        ComplaintResponse response = complaintService.assignComplaint(id, request, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Complaint assigned successfully", response));
    }

    // ── Auto-assign complaint ────────────────────────────────────────────
    @PatchMapping("/{id}/auto-assign")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_MANAGER')")
    public ResponseEntity<ApiResponse<ComplaintResponse>> autoAssign(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        ComplaintResponse response = complaintService.autoAssignComplaint(id, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Complaint auto-assigned", response));
    }

    // ── Delete complaint (Admin only) ────────────────────────────────────
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteComplaint(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        complaintService.deleteComplaint(id, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Complaint deleted", null));
    }
}
