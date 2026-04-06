package com.studentreview.server.controller;

import com.studentreview.server.dto.ApiResponse;
import com.studentreview.server.dto.VoteRequest;
import com.studentreview.server.service.VoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews")
public class VoteController {

    @Autowired
    private VoteService voteService;

    @PostMapping("/{id}/vote")
    public ResponseEntity<ApiResponse> castVote(@PathVariable Long id, @RequestBody VoteRequest request) {
        try {
            voteService.castVote(id, request);
            return ResponseEntity.ok(new ApiResponse(true, "Vote cast successfully", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, e.getMessage(), null));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(new ApiResponse(false, "Error casting vote", null));
        }
    }
}
