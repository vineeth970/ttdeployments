package com.scrms.service;

import com.scrms.dto.request.CommentRequest;
import com.scrms.dto.response.CommentResponse;

import java.util.List;

public interface CommentService {
    CommentResponse addComment(Long complaintId, CommentRequest request, String authorEmail);
    List<CommentResponse> getCommentsByComplaint(Long complaintId, String userEmail);
    void deleteComment(Long commentId, String userEmail);
}
