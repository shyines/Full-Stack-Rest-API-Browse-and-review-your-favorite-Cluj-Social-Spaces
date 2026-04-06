package com.studentreview.server.service;

import com.studentreview.server.dto.ReplyRequest;
import com.studentreview.server.dto.ReplyResponse;
import com.studentreview.server.dto.ReviewRequest;
import com.studentreview.server.dto.ReviewResponse;
import com.studentreview.server.model.Place;
import com.studentreview.server.model.Review;
import com.studentreview.server.model.ReviewReply;
import com.studentreview.server.model.User;
import com.studentreview.server.repository.PlaceRepository;
import com.studentreview.server.repository.ReviewReplyRepository;
import com.studentreview.server.repository.ReviewRepository;
import com.studentreview.server.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private PlaceRepository placeRepository;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private VoteService voteService;

    @Autowired
    private ReviewReplyRepository reviewReplyRepository;

    @Transactional
    public ReviewResponse addReview(Long placeId, ReviewRequest request) {
        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new IllegalArgumentException("Place not found"));

        User user = userRepository.findByEmail(request.getUserEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (user.getRole() != User.UserRole.STUDENT) {
            throw new IllegalArgumentException("Only students can leave reviews");
        }

        if (reviewRepository.existsByPlaceIdAndUserId(placeId, user.getId())) {
            throw new IllegalArgumentException("You have already reviewed this place");
        }

        Review review = new Review();
        review.setPlace(place);
        review.setUser(user);
        review.setRating(request.getRating());
        review.setComment(request.getComment());

        review = reviewRepository.save(review);

        return new ReviewResponse(
                review.getId(),
                user.getUsername(),
                review.getRating(),
                review.getComment(),
                review.getCreatedAt(),
                0, 0, null,
                new ArrayList<>()
        );
    }

    @Transactional
    public ReviewResponse updateReview(Long reviewId, ReviewRequest request) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found"));

        User user = userRepository.findByEmail(request.getUserEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!review.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("You can only edit your own reviews");
        }

        review.setRating(request.getRating());
        review.setComment(request.getComment());
        
        review = reviewRepository.save(review);

        // Fetch votes for this updated review
        List<Long> idList = List.of(review.getId());
        Map<Long, Integer> upvotes = voteService.getUpvotesForReviews(idList);
        Map<Long, Integer> downvotes = voteService.getDownvotesForReviews(idList);
        Map<Long, String> userVote = voteService.getUserVotesForReviews(idList, request.getUserEmail());

        // Fetch replies
        List<ReplyResponse> replies = reviewReplyRepository.findByReviewIdOrderByCreatedAtAsc(review.getId())
                .stream()
                .map(this::mapToReplyResponse)
                .collect(Collectors.toList());

        return new ReviewResponse(
                review.getId(),
                user.getUsername(),
                review.getRating(),
                review.getComment(),
                review.getCreatedAt(),
                upvotes.getOrDefault(review.getId(), 0),
                downvotes.getOrDefault(review.getId(), 0),
                userVote.get(review.getId()),
                replies
        );
    }

    public List<ReviewResponse> getReviewsForPlace(Long placeId, String currentUserEmail) {
        List<Review> reviews = reviewRepository.findByPlaceIdOrderByCreatedAtDesc(placeId);
        List<Long> reviewIds = reviews.stream().map(Review::getId).collect(Collectors.toList());

        if (reviewIds.isEmpty()) {
            return List.of();
        }

        Map<Long, Integer> upvotes = voteService.getUpvotesForReviews(reviewIds);
        Map<Long, Integer> downvotes = voteService.getDownvotesForReviews(reviewIds);
        Map<Long, String> userVotes = voteService.getUserVotesForReviews(reviewIds, currentUserEmail);

        // Fetch all replies for this place
        List<ReviewReply> allReplies = reviewReplyRepository.findByReview_Place_Id(placeId);
        Map<Long, List<ReplyResponse>> repliesByReviewId = allReplies.stream()
            .collect(Collectors.groupingBy(
                reply -> reply.getReview().getId(),
                Collectors.mapping(this::mapToReplyResponse, Collectors.toList())
            ));

        return reviews.stream()
                .map(r -> new ReviewResponse(
                        r.getId(),
                        r.getUser().getUsername(),
                        r.getRating(),
                        r.getComment(),
                        r.getCreatedAt(),
                        upvotes.getOrDefault(r.getId(), 0),
                        downvotes.getOrDefault(r.getId(), 0),
                        userVotes.get(r.getId()),
                        repliesByReviewId.getOrDefault(r.getId(), new ArrayList<>())
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public ReplyResponse addReply(Long reviewId, ReplyRequest request) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found"));

        User user = userRepository.findByEmail(request.getUserEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        ReviewReply reply = new ReviewReply();
        reply.setReview(review);
        reply.setUser(user);
        reply.setComment(request.getComment());

        reply = reviewReplyRepository.save(reply);

        return mapToReplyResponse(reply);
    }

    private ReplyResponse mapToReplyResponse(ReviewReply reply) {
        boolean isOwner = false;
        Place place = reply.getReview().getPlace();
        if (place.getOwner() != null && place.getOwner().getId().equals(reply.getUser().getId())) {
            isOwner = true;
        }

        return new ReplyResponse(
                reply.getId(),
                reply.getUser().getUsername(),
                reply.getComment(),
                reply.getCreatedAt(),
                reply.getUser().getRole().toString(),
                isOwner
        );
    }
}