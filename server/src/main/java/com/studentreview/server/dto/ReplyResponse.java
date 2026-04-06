package com.studentreview.server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReplyResponse {
    private Long id;
    private String username;
    private String comment;
    private LocalDateTime createdAt;
    private String userRole;
    @com.fasterxml.jackson.annotation.JsonProperty("owner")
    private boolean isOwner;
}
