package com.studentreview.server.dto;

import lombok.Data;

@Data
public class ReviewRequest {
    private Integer rating;
    private String comment;
    private String userEmail; // In a real app with security, we'd get this from the session/token
}
