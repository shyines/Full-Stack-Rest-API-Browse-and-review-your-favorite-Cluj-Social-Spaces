package com.studentreview.server.dto;

import lombok.Data;

@Data
public class VoteRequest {
    private String userEmail;
    private String voteType; // UPVOTE or DOWNVOTE
}
