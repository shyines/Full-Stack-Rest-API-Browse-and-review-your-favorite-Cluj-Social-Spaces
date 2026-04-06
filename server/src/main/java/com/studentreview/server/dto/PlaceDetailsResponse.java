package com.studentreview.server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlaceDetailsResponse {
    private Long id;
    private String name;
    private String description;
    private String address;
    private String type;
    private String ownerName;
    private List<ReviewResponse> reviews;
    private Double averageRating;
    
    @com.fasterxml.jackson.annotation.JsonProperty("favorited")
    private boolean isFavorited;
}
