package com.studentreview.server.service;

import com.studentreview.server.dto.VoteRequest;
import com.studentreview.server.model.Review;
import com.studentreview.server.model.ReviewVote;
import com.studentreview.server.model.User;
import com.studentreview.server.model.VoteType;
import com.studentreview.server.repository.ReviewRepository;
import com.studentreview.server.repository.ReviewVoteRepository;
import com.studentreview.server.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class VoteService {

    @Autowired
    private ReviewVoteRepository voteRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public void castVote(Long reviewId, VoteRequest request) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found"));

        User user = userRepository.findByEmail(request.getUserEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Optional<ReviewVote> existingVote = voteRepository.findByReviewAndUser(review, user);
        VoteType newType = VoteType.valueOf(request.getVoteType());

        if (existingVote.isPresent()) {
            ReviewVote vote = existingVote.get();
            if (vote.getType() == newType) {
                // Toggle off if same vote
                voteRepository.delete(vote);
            } else {
                // Change vote
                vote.setType(newType);
                voteRepository.save(vote);
            }
        } else {
            ReviewVote vote = new ReviewVote();
            review.getPlace().getId(); // Ensure lazy load if needed, though not strictly necessary here
            vote.setReview(review);
            vote.setUser(user);
            vote.setType(newType);
            voteRepository.save(vote);
        }
    }

    public Map<Long, Integer> getUpvotesForReviews(List<Long> reviewIds) {
        List<ReviewVote> votes = voteRepository.findAllByReviewIds(reviewIds);
        return reviewIds.stream().collect(Collectors.toMap(
                id -> id,
                id -> (int) votes.stream().filter(v -> v.getReview().getId().equals(id) && v.getType() == VoteType.UPVOTE).count()
        ));
    }

    public Map<Long, Integer> getDownvotesForReviews(List<Long> reviewIds) {
        List<ReviewVote> votes = voteRepository.findAllByReviewIds(reviewIds);
        return reviewIds.stream().collect(Collectors.toMap(
                id -> id,
                id -> (int) votes.stream().filter(v -> v.getReview().getId().equals(id) && v.getType() == VoteType.DOWNVOTE).count()
        ));
    }
    
    public Map<Long, String> getUserVotesForReviews(List<Long> reviewIds, String userEmail) {
        if (userEmail == null) return Map.of();
        
        Optional<User> user = userRepository.findByEmail(userEmail);
        if (user.isEmpty()) return Map.of();

        Long userId = user.get().getId();
        List<ReviewVote> votes = voteRepository.findAllByReviewIds(reviewIds);
        
        // Filter only votes made by this user to avoid null values in toMap
        return votes.stream()
                .filter(v -> v.getUser().getId().equals(userId))
                .collect(Collectors.toMap(
                        v -> v.getReview().getId(),
                        v -> v.getType().name(),
                        (existing, replacement) -> existing // In case of duplicates, keep existing
                ));
    }
}
