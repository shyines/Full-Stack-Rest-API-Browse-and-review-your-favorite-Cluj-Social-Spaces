package com.studentreview.server.controller;

import com.studentreview.server.dto.ApiResponse;
import com.studentreview.server.dto.PlaceDetailsResponse;
import com.studentreview.server.dto.PlaceResponse;
import com.studentreview.server.dto.ReviewRequest;
import com.studentreview.server.dto.ReviewResponse;
import com.studentreview.server.model.Place;
import com.studentreview.server.repository.PlaceRepository;
import com.studentreview.server.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/places")
public class PlaceController {

    @Autowired
    private PlaceRepository placeRepository;

    @Autowired
    private ReviewService reviewService;

    /**
     * Get all active places
     * GET /api/places?search=...&type=...&minRating=...&minReviews=...
     */
    @GetMapping
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse> getAllActivePlaces(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Double minRating,
            @RequestParam(required = false) Integer minReviews) {
        try {
            List<Place> places;
            if (search != null && !search.trim().isEmpty()) {
                places = placeRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(search, search);
            } else {
                places = placeRepository.findAll();
            }

            List<PlaceResponse> activePlaces = places.stream()
                    .filter(Place::getIsActive)
                    .filter(p -> {
                        if (type == null || type.isEmpty()) return true;
                        String placeType = p.getType() != null ? p.getType().toString() : "OTHER";
                        return placeType.equals(type);
                    })
                    .map(this::convertToDto)
                    .filter(dto -> minRating == null || dto.getAverageRating() >= minRating)
                    .filter(dto -> minReviews == null || dto.getReviewCount() >= minReviews)
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(new ApiResponse(true, "Places retrieved successfully", activePlaces));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(new ApiResponse(false, "Error retrieving places: " + e.getMessage(), null));
        }
    }

    /**
     * Get place details with reviews
     * GET /api/places/{id}?userEmail=...
     */
    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse> getPlaceDetails(@PathVariable Long id, @RequestParam(required = false) String userEmail) {
        try {
            Place place = placeRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Place not found"));

            List<ReviewResponse> reviews = reviewService.getReviewsForPlace(id, userEmail);
            
            Double averageRating = reviews.stream()
                    .mapToInt(ReviewResponse::getRating)
                    .average()
                    .orElse(0.0);

            String ownerName = "Unknown";
            if (place.getOwner() != null) {
                ownerName = place.getOwner().getBusinessName();
                if (ownerName == null || ownerName.isEmpty()) {
                    ownerName = place.getOwner().getUsername();
                }
            }

            String placeType = place.getType() != null ? place.getType().toString() : "OTHER";

            boolean isFavorited = false;
            if (userEmail != null) {
                isFavorited = place.getFavoritedBy() != null && 
                    place.getFavoritedBy().stream().anyMatch(u -> u.getEmail().equals(userEmail));
            }

            PlaceDetailsResponse response = new PlaceDetailsResponse(
                    place.getId(),
                    place.getName(),
                    place.getDescription(),
                    place.getAddress(),
                    placeType,
                    ownerName,
                    reviews,
                    averageRating,
                    isFavorited
            );

            return ResponseEntity.ok(new ApiResponse(true, "Place details retrieved", response));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(new ApiResponse(false, e.getMessage(), null));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(new ApiResponse(false, "Error retrieving place details", null));
        }
    }

// ... existing code ...

    /**
     * Add a review to a place
     * POST /api/places/{id}/reviews
     */
    @PostMapping("/{id}/reviews")
    public ResponseEntity<ApiResponse> addReview(@PathVariable Long id, @RequestBody ReviewRequest request) {
        try {
            ReviewResponse review = reviewService.addReview(id, request);
            return ResponseEntity.ok(new ApiResponse(true, "Review added successfully", review));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage(), null));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(new ApiResponse(false, "Error adding review", null));
        }
    }

    /**
     * Update a review
     * PUT /api/places/{placeId}/reviews/{reviewId}
     */
    @PutMapping("/{placeId}/reviews/{reviewId}")
    public ResponseEntity<ApiResponse> updateReview(
            @PathVariable Long placeId,
            @PathVariable Long reviewId,
            @RequestBody ReviewRequest request) {
        try {
            ReviewResponse review = reviewService.updateReview(reviewId, request);
            return ResponseEntity.ok(new ApiResponse(true, "Review updated successfully", review));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage(), null));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(new ApiResponse(false, "Error updating review", null));
        }
    }

    private PlaceResponse convertToDto(Place place) {
        String ownerName = "Unknown";
        if (place.getOwner() != null) {
            ownerName = place.getOwner().getBusinessName();
            if (ownerName == null || ownerName.isEmpty()) {
                ownerName = place.getOwner().getUsername();
            }
        }

        String placeType = place.getType() != null ? place.getType().toString() : "OTHER";
        
        Double averageRating = 0.0;
        Integer reviewCount = 0;
        
        if (place.getReviews() != null && !place.getReviews().isEmpty()) {
            reviewCount = place.getReviews().size();
            averageRating = place.getReviews().stream()
                    .mapToInt(com.studentreview.server.model.Review::getRating)
                    .average()
                    .orElse(0.0);
        }

        return new PlaceResponse(
            place.getId(),
            place.getName(),
            place.getDescription(),
            place.getAddress(),
            placeType,
            ownerName,
            averageRating,
            reviewCount
        );
    }
}
