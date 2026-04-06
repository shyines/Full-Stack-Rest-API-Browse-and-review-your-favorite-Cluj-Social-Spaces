package com.studentreview.server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileResponse {
    private String username;
    private String role;
    private LocalDateTime joinDate;
    private int totalReviews;
    private double averageRatingGiven;
    private List<ReviewResponse> recentReviews;
    private List<PlaceResponse> favoritePlaces;
}
