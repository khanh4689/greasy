package com.gearsy.gearsy.service.impl;

import com.gearsy.gearsy.entity.Products;
import com.gearsy.gearsy.entity.Reviews;
import com.gearsy.gearsy.entity.Users;
import com.gearsy.gearsy.repository.ProductsRepository;
import com.gearsy.gearsy.repository.ReviewRepository;
import com.gearsy.gearsy.repository.UsersRepository;
import com.gearsy.gearsy.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductsRepository productsRepository;
    private final UsersRepository usersRepository;


    @Override
    public List<Reviews> getReviewsByProductId(Long productId) {
        return reviewRepository.findByProductIdWithUser(productId);
    }

    public List<Reviews> getAllReviews() {
        return reviewRepository.findAllByOrderByReviewIdAsc();
    }

    @Override
    public void submitReview(Long productId, int rating, String comment, String userEmail) {
        Users user = usersRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Products product = productsRepository.findById(productId).orElseThrow();

        Reviews review = new Reviews();
        review.setUser(user);
        review.setProduct(product);
        review.setRating(rating);
        review.setComment(comment);
        review.setCreatedAt(LocalDateTime.now());

        reviewRepository.save(review);
    }

    @Override
    public void updateReview(Long reviewId, int rating, String comment, String email) {
        Reviews rv = reviewRepository.findByReviewIdAndUser_Email(reviewId,email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy review của bạn"));

        rv.setRating(rating);
        rv.setComment(comment);
        rv.setUpdatedAt(LocalDateTime.now());
        reviewRepository.save(rv);
    }

    @Override
    public void deleteReviewByEmail(Long reviewId, String email) {
        Reviews rv = reviewRepository.findByReviewIdAndUser_Email(reviewId,email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy review của bạn"));
        reviewRepository.delete(rv);
    }

    @Override
    public Optional<Reviews> getReviewByIdAndUserEmail(Long reviewId, String userEmail) {
        return reviewRepository.findByReviewIdAndUserEmail(reviewId, userEmail);
    }

    @Override
    public void hideReview(Long reviewId) {
        Reviews review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy bình luận với ID: " + reviewId));
        // Chuyển đổi trạng thái
        review.setStatus(review.getStatus().equals("VISIBLE") ? "HIDDEN" : "VISIBLE");
        review.setUpdatedAt(LocalDateTime.now());
        reviewRepository.save(review);
    }

    @Override
    public void deleteReview(Long reviewId) {
        Reviews review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy bình luận với ID: " + reviewId));
        reviewRepository.delete(review);
    }

    public List<Reviews> getAllVisibleReviews() {
        return reviewRepository.findAllByStatusOrderByReviewIdAsc("VISIBLE");
    }

    @Override
    public List<Reviews> searchReviews(String keyword) {
        return reviewRepository.searchByKeyword(keyword);
    }

}
