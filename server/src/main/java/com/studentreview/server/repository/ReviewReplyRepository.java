package com.studentreview.server.repository;

import com.studentreview.server.model.ReviewReply;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReviewReplyRepository extends JpaRepository<ReviewReply, Long> {
    List<ReviewReply> findByReviewIdOrderByCreatedAtAsc(Long reviewId);
    List<ReviewReply> findByReview_Place_Id(Long placeId);
}
