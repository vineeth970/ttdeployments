package com.scrms.service;

import com.scrms.dto.request.AssignRequest;
import com.scrms.dto.request.ComplaintRequest;
import com.scrms.dto.request.StatusUpdateRequest;
import com.scrms.dto.response.ComplaintResponse;

import java.util.List;

public interface ComplaintService {
    ComplaintResponse createComplaint(ComplaintRequest request, String customerEmail);
    ComplaintResponse getComplaintById(Long id, String userEmail);
    ComplaintResponse getComplaintByReference(String referenceNumber);
    List<ComplaintResponse> getAllComplaints(String userEmail);
    List<ComplaintResponse> getMyComplaints(String customerEmail);
    List<ComplaintResponse> getAgentComplaints(String agentEmail);
    ComplaintResponse updateStatus(Long id, StatusUpdateRequest request, String userEmail);
    ComplaintResponse assignComplaint(Long id, AssignRequest request, String managerEmail);
    ComplaintResponse autoAssignComplaint(Long id, String managerEmail);
    void deleteComplaint(Long id, String adminEmail);
}
