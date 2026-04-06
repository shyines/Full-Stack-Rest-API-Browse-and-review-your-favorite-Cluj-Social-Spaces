package com.studentreview.server.controller;

import com.studentreview.server.dto.ApiResponse;
import com.studentreview.server.dto.PlaceRequest;
import com.studentreview.server.dto.PlaceResponse;
import com.studentreview.server.service.OwnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/owner")
public class OwnerController {

    @Autowired
    private OwnerService ownerService;

    /**
     * Get places for a specific owner
     * GET /api/owner/places
     */
    @GetMapping("/places")
    public ResponseEntity<ApiResponse> getMyPlaces(@RequestParam String email) {
        try {
            List<PlaceResponse> places = ownerService.getOwnerPlaces(email);
            return ResponseEntity.ok(new ApiResponse(true, "Places retrieved", places));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    /**
     * Create a new place
     * POST /api/owner/places
     */
    @PostMapping("/places")
    public ResponseEntity<ApiResponse> createPlace(@RequestBody PlaceRequest request) {
        try {
            PlaceResponse place = ownerService.createPlace(request);
            return ResponseEntity.ok(new ApiResponse(true, "Place created successfully", place));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ApiResponse(false, "Error creating place", null));
        }
    }

    /**
     * Update a place
     * PUT /api/owner/places/{id}
     */
    @PutMapping("/places/{id}")
    public ResponseEntity<ApiResponse> updatePlace(@PathVariable Long id, @RequestBody PlaceRequest request) {
        try {
            PlaceResponse place = ownerService.updatePlace(id, request);
            return ResponseEntity.ok(new ApiResponse(true, "Place updated successfully", place));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ApiResponse(false, "Error updating place", null));
        }
    }
}
