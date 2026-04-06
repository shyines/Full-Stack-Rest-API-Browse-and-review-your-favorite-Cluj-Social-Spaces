package com.studentreview.server.service;

import com.studentreview.server.dto.UserResponse;
import com.studentreview.server.model.Place;
import com.studentreview.server.model.User;
import com.studentreview.server.repository.PlaceRepository;
import com.studentreview.server.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PlaceRepository placeRepository;

    @Autowired
    private com.studentreview.server.repository.ReviewRepository reviewRepository;

    /**
     * Get all users
     */
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToUserResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get users by role
     */
    public List<UserResponse> getUsersByRole(String role) {
        try {
            User.UserRole userRole = User.UserRole.valueOf(role.toUpperCase());
            return userRepository.findByRole(userRole).stream()
                    .map(this::convertToUserResponse)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid role");
        }
    }

    /**
     * Deactivate a user
     */
    @Transactional
    public UserResponse deactivateUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Cannot deactivate admins
        if (user.getRole() == User.UserRole.ADMIN) {
            throw new IllegalArgumentException("Cannot deactivate admin users");
        }

        user.setIsActive(false);
        user = userRepository.save(user);

        return convertToUserResponse(user);
    }

    /**
     * Activate a user
     */
    @Transactional
    public UserResponse activateUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.setIsActive(true);
        user = userRepository.save(user);

        return convertToUserResponse(user);
    }

    /**
     * Delete a user (soft delete)
     */
    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Cannot delete admins
        if (user.getRole() == User.UserRole.ADMIN) {
            throw new IllegalArgumentException("Cannot delete admin users");
        }

        user.setIsActive(false);
        userRepository.save(user);
    }

    /**
     * Get all places (including unapproved)
     */
    public List<Place> getAllPlaces() {
        return placeRepository.findAll();
    }

    /**
     * Deactivate a place
     */
    @Transactional
    public Place deactivatePlace(Long placeId) {
        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new IllegalArgumentException("Place not found"));

        place.setIsActive(false);
        return placeRepository.save(place);
    }

    /**
     * Activate a place
     */
    @Transactional
    public Place activatePlace(Long placeId) {
        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new IllegalArgumentException("Place not found"));

        place.setIsActive(true);
        return placeRepository.save(place);
    }

    /**
     * Delete a review
     */
    @Transactional
    public void deleteReview(Long reviewId) {
        if (!reviewRepository.existsById(reviewId)) {
            throw new IllegalArgumentException("Review not found");
        }
        reviewRepository.deleteById(reviewId);
    }

    /**
     * Convert User entity to UserResponse DTO
     */
    private UserResponse convertToUserResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole().toString(),
                user.getBusinessName(),
                user.getIsActive()
        );
    }
}