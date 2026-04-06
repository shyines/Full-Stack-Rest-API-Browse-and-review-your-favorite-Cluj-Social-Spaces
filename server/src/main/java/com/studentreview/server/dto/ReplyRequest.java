package com.studentreview.server.dto;

import lombok.Data;

@Data
public class ReplyRequest {
    private String comment;
    private String userEmail;
}
