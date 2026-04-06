package com.studentreview.server.repository;

import com.studentreview.server.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByPlaceIdOrderByCreatedAtDesc(Long placeId);
    List<Review> findByUser_UsernameOrderByCreatedAtDesc(String username);
    boolean existsByPlaceIdAndUserId(Long placeId, Long userId);
}
