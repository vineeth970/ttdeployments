package com.scrms.service.impl;

import com.scrms.dto.request.CommentRequest;
import com.scrms.dto.response.CommentResponse;
import com.scrms.entity.Comment;
import com.scrms.entity.Complaint;
import com.scrms.entity.User;
import com.scrms.enums.Role;
import com.scrms.exception.ResourceNotFoundException;
import com.scrms.exception.UnauthorizedException;
import com.scrms.repository.CommentRepository;
import com.scrms.repository.ComplaintRepository;
import com.scrms.repository.UserRepository;
import com.scrms.service.CommentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final ComplaintRepository complaintRepository;
    private final UserRepository userRepository;

    // Manual constructor injection (replaces @RequiredArgsConstructor)
    public CommentServiceImpl(CommentRepository commentRepository, ComplaintRepository complaintRepository, UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.complaintRepository = complaintRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public CommentResponse addComment(Long complaintId, CommentRequest request, String authorEmail) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint", complaintId));
        User author = userRepository.findByEmail(authorEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + authorEmail));

        // Customers cannot add internal notes
        boolean isInternal = request.isInternal();
        if (author.getRole() == Role.ROLE_CUSTOMER && isInternal) {
            isInternal = false;
        }

        Comment comment = new Comment();
        comment.setComplaint(complaint);
        comment.setAuthor(author);
        comment.setContent(request.getContent());
        comment.setInternal(isInternal);

        Comment saved = commentRepository.save(comment);
        return mapToResponse(saved);
    }

    @Override
    public List<CommentResponse> getCommentsByComplaint(Long complaintId, String userEmail) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint", complaintId));
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<Comment> comments;
        if (user.getRole() == Role.ROLE_CUSTOMER) {
            // Customers only see public comments
            comments = commentRepository.findByComplaintAndInternalFalseOrderByCreatedAtAsc(complaint);
        } else {
            // Staff see all comments
            comments = commentRepository.findByComplaintOrderByCreatedAtAsc(complaint);
        }

        return comments.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId, String userEmail) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", commentId));
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        boolean isOwner = comment.getAuthor().getEmail().equals(userEmail);
        boolean isAdmin = user.getRole() == Role.ROLE_ADMIN;

        if (!isOwner && !isAdmin) {
            throw new UnauthorizedException("You cannot delete this comment");
        }

        commentRepository.delete(comment);
    }

    private CommentResponse mapToResponse(Comment comment) {
        CommentResponse response = new CommentResponse();
        response.setId(comment.getId());
        response.setContent(comment.getContent());
        response.setInternal(comment.isInternal());
        response.setAuthor(AuthServiceImpl.mapToUserResponse(comment.getAuthor()));
        response.setCreatedAt(comment.getCreatedAt());
        return response;
    }
}
