package com.gearsy.gearsy.repository;

import com.gearsy.gearsy.entity.Reviews;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Reviews, Long> {
    List<Reviews> findByProduct_ProductIdOrderByCreatedAtDesc(Long productId);
    @Query("SELECT r FROM Reviews r JOIN FETCH r.user WHERE r.product.productId = :productId")
    List<Reviews> findByProductIdWithUser(@Param("productId") Long productId);

    Optional<Reviews> findByReviewIdAndUser_Email(Long reviewId, String email);

    Optional<Reviews> findByReviewIdAndUserEmail(Long reviewId, String userEmail);
}
