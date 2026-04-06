package com.studentreview.server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlaceResponse {
    private Long id;
    private String name;
    private String description;
    private String address;
    private String type;
    private String ownerName;
    private Double averageRating;
    private Integer reviewCount;
}
