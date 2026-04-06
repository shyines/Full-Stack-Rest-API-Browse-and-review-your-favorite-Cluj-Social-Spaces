package com.studentreview.server.dto;

import lombok.Data;

@Data
public class PlaceRequest {
    private String name;
    private String description;
    private String address;
    private String type;
    private String ownerEmail; // To identify the owner
}
