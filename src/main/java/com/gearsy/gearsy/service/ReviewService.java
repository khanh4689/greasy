package com.gearsy.gearsy.service;

import com.gearsy.gearsy.entity.Reviews;

import java.util.List;
import java.util.Optional;

public interface ReviewService {
    List<Reviews> getReviewsByProductId(Long productId);
    void submitReview(Long productId, int rating, String comment, String userEmail);

    void updateReview(Long reviewId, int rating, String comment, String userEmail);

    void deleteReview(Long reviewId, String email);

    Optional<Reviews> getReviewByIdAndUserEmail(Long reviewId, String userEmail);

}
