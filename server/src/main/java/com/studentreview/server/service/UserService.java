package com.studentreview.server.service;

import com.studentreview.server.dto.PlaceResponse;
import com.studentreview.server.dto.ReviewResponse;
import com.studentreview.server.dto.UserProfileResponse;
import com.studentreview.server.model.Place;
import com.studentreview.server.model.Review;
import com.studentreview.server.model.User;
import com.studentreview.server.repository.PlaceRepository;
import com.studentreview.server.repository.ReviewRepository;
import com.studentreview.server.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReviewRepository reviewRepository;
    
    @Autowired
    private PlaceRepository placeRepository;
    
    @Autowired
    private VoteService voteService;

    @Transactional(readOnly = true)
    public UserProfileResponse getUserProfile(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<Review> reviews = reviewRepository.findByUser_UsernameOrderByCreatedAtDesc(username);

        double averageRating = reviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);
        
        List<Long> reviewIds = reviews.stream().map(Review::getId).collect(Collectors.toList());
        var upvotes = voteService.getUpvotesForReviews(reviewIds);
        var downvotes = voteService.getDownvotesForReviews(reviewIds);

        List<ReviewResponse> reviewDtos = reviews.stream()
                .map(r -> new ReviewResponse(
                        r.getId(),
                        r.getUser().getUsername(),
                        r.getRating(),
                        r.getComment(),
                        r.getCreatedAt(),
                        upvotes.getOrDefault(r.getId(), 0),
                        downvotes.getOrDefault(r.getId(), 0),
                        null,
                        new java.util.ArrayList<>()
                ))
                .collect(Collectors.toList());

        List<PlaceResponse> favoritePlaces = user.getFavoritePlaces().stream()
                .map(this::convertToPlaceDto)
                .collect(Collectors.toList());

        return new UserProfileResponse(
                user.getUsername(),
                user.getRole().toString(),
                user.getCreatedAt(),
                reviews.size(),
                averageRating,
                reviewDtos,
                favoritePlaces
        );
    }

    @Transactional
    public boolean toggleFavorite(String userEmail, Long placeId) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new IllegalArgumentException("Place not found"));
        
        boolean isFavorite;
        if (user.getFavoritePlaces().contains(place)) {
            user.getFavoritePlaces().remove(place);
            isFavorite = false;
        } else {
            user.getFavoritePlaces().add(place);
            isFavorite = true;
        }
        
        userRepository.save(user);
        return isFavorite;
    }

    private PlaceResponse convertToPlaceDto(Place place) {
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
                    .mapToInt(Review::getRating)
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