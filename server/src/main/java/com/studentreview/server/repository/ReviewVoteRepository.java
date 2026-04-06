package com.studentreview.server.repository;

import com.studentreview.server.model.Review;
import com.studentreview.server.model.ReviewVote;
import com.studentreview.server.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReviewVoteRepository extends JpaRepository<ReviewVote, Long> {
    Optional<ReviewVote> findByReviewAndUser(Review review, User user);
    
    List<ReviewVote> findByReview(Review review);
    
    @Query("SELECT v FROM ReviewVote v WHERE v.review.id IN :reviewIds")
    List<ReviewVote> findAllByReviewIds(@Param("reviewIds") List<Long> reviewIds);
}
