package com.studentreview.server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewResponse {
    private Long id;
    private String username;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
    
    // New fields
    private int upvotes;
    private int downvotes;
    private String currentUserVote; // "UPVOTE", "DOWNVOTE", or null
    
    private java.util.List<ReplyResponse> replies;
}