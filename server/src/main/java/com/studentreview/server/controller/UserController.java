package com.studentreview.server.controller;

import com.studentreview.server.dto.ApiResponse;
import com.studentreview.server.dto.UserProfileResponse;
import com.studentreview.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/{username}")
    public ResponseEntity<ApiResponse> getUserProfile(@PathVariable String username) {
        try {
            UserProfileResponse profile = userService.getUserProfile(username);
            return ResponseEntity.ok(new ApiResponse(true, "Profile retrieved", profile));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(new ApiResponse(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ApiResponse(false, "Error retrieving profile", null));
        }
    }

    @PostMapping("/favorites/{placeId}")
    public ResponseEntity<ApiResponse> toggleFavorite(@PathVariable Long placeId, @RequestBody java.util.Map<String, String> request) {
        try {
            String userEmail = request.get("userEmail");
            boolean isFavorite = userService.toggleFavorite(userEmail, placeId);
            return ResponseEntity.ok(new ApiResponse(true, isFavorite ? "Place added to favorites" : "Place removed from favorites", isFavorite));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(new ApiResponse(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ApiResponse(false, "Error toggling favorite", null));
        }
    }
}
