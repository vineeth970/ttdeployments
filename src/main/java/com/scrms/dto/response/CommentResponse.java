package com.scrms.dto.response;

import java.time.LocalDateTime;

public class CommentResponse {
    private Long id;
    private String content;
    private boolean internal;
    private UserResponse author;
    private LocalDateTime createdAt;

    // Default Constructor
    public CommentResponse() {}

    // All-args Constructor
    public CommentResponse(Long id, String content, boolean internal, UserResponse author, LocalDateTime createdAt) {
        this.id = id;
        this.content = content;
        this.internal = internal;
        this.author = author;
        this.createdAt = createdAt;
    }


    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public boolean isInternal() { return internal; }
    public void setInternal(boolean internal) { this.internal = internal; }

    public UserResponse getAuthor() { return author; }
    public void setAuthor(UserResponse author) { this.author = author; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

}
