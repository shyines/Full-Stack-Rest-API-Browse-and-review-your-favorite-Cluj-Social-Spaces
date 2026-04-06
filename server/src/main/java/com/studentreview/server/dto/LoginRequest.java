package com.studentreview.server.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String email; // or username, usually email is unique
    private String password;
}
