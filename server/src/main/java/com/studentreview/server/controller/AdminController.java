package com.studentreview.server.controller;

import com.studentreview.server.dto.ApiResponse;
import com.studentreview.server.dto.UserResponse;
import com.studentreview.server.model.Place;
import com.studentreview.server.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    /**
     * Get all users
     * GET /api/admin/users
     */
    @GetMapping("/users")
    public ResponseEntity<ApiResponse> getAllUsers() {
        try {
            List<UserResponse> users = adminService.getAllUsers();
            return ResponseEntity.ok(new ApiResponse(true, "Users retrieved successfully", users));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Error retrieving users", null));
        }
    }

    /**
     * Get users by role
     * GET /api/admin/users/role/{role}
     */
    @GetMapping("/users/role/{role}")
    public ResponseEntity<ApiResponse> getUsersByRole(@PathVariable String role) {
        try {
            List<UserResponse> users = adminService.getUsersByRole(role);
            return ResponseEntity.ok(new ApiResponse(true, "Users retrieved successfully", users));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Error retrieving users", null));
        }
    }

    /**
     * Deactivate a user
     * PUT /api/admin/users/{id}/deactivate
     */
    @PutMapping("/users/{id}/deactivate")
    public ResponseEntity<ApiResponse> deactivateUser(@PathVariable Long id) {
        try {
            UserResponse user = adminService.deactivateUser(id);
            return ResponseEntity.ok(new ApiResponse(true, "User deactivated successfully", user));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Error deactivating user", null));
        }
    }

    /**
     * Activate a user
     * PUT /api/admin/users/{id}/activate
     */
    @PutMapping("/users/{id}/activate")
    public ResponseEntity<ApiResponse> activateUser(@PathVariable Long id) {
        try {
            UserResponse user = adminService.activateUser(id);
            return ResponseEntity.ok(new ApiResponse(true, "User activated successfully", user));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Error activating user", null));
        }
    }

    /**
     * Delete a user
     * DELETE /api/admin/users/{id}
     */
    @DeleteMapping("/users/{id}")
    public ResponseEntity<ApiResponse> deleteUser(@PathVariable Long id) {
        try {
            adminService.deleteUser(id);
            return ResponseEntity.ok(new ApiResponse(true, "User deleted successfully", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Error deleting user", null));
        }
    }

    /**
     * Get all places (including unapproved)
     * GET /api/admin/places
     */
    @GetMapping("/places")
    public ResponseEntity<ApiResponse> getAllPlaces() {
        try {
            List<Place> places = adminService.getAllPlaces();
            return ResponseEntity.ok(new ApiResponse(true, "Places retrieved successfully", places));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Error retrieving places", null));
        }
    }

    /**
     * Deactivate a place
     * PUT /api/admin/places/{id}/deactivate
     */
    @PutMapping("/places/{id}/deactivate")
    public ResponseEntity<ApiResponse> deactivatePlace(@PathVariable Long id) {
        try {
            Place place = adminService.deactivatePlace(id);
            return ResponseEntity.ok(new ApiResponse(true, "Place deactivated successfully", place));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Error deactivating place", null));
        }
    }

    /**
     * Activate a place
     * PUT /api/admin/places/{id}/activate
     */
    @PutMapping("/places/{id}/activate")
    public ResponseEntity<ApiResponse> activatePlace(@PathVariable Long id) {
        try {
            Place place = adminService.activatePlace(id);
            return ResponseEntity.ok(new ApiResponse(true, "Place activated successfully", place));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Error activating place", null));
        }
    }

    /**
     * Delete a review
     * DELETE /api/admin/reviews/{id}
     */
    @DeleteMapping("/reviews/{id}")
    public ResponseEntity<ApiResponse> deleteReview(@PathVariable Long id) {
        try {
            adminService.deleteReview(id);
            return ResponseEntity.ok(new ApiResponse(true, "Review deleted successfully", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Error deleting review", null));
        }
    }
}