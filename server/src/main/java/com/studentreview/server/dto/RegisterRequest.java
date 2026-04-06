package com.studentreview.server.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String email;
    private String password;
    private String role; // "STUDENT", "OWNER"
    private String businessName; // Optional, for OWNER
}
