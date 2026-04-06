package com.studentreview.server.controller;

import com.studentreview.server.dto.ApiResponse;
import com.studentreview.server.dto.ReplyRequest;
import com.studentreview.server.dto.ReplyResponse;
import com.studentreview.server.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @PostMapping("/{reviewId}/replies")
    public ResponseEntity<ApiResponse> addReply(
            @PathVariable Long reviewId,
            @RequestBody ReplyRequest request) {
        try {
            ReplyResponse reply = reviewService.addReply(reviewId, request);
            return ResponseEntity.ok(new ApiResponse(true, "Reply added successfully", reply));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage(), null));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(new ApiResponse(false, "Error adding reply", null));
        }
    }
}
