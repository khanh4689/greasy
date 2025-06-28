package com.gearsy.gearsy.repository;

import com.gearsy.gearsy.entity.Promotions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
@Repository
public interface PromotionRepository extends JpaRepository<Promotions,Long> {
    @Query("""
SELECT pp.promotion FROM ProductPromotions pp
WHERE pp.product.productId = :productId
  AND pp.promotion.status = 'Active'
  AND :now BETWEEN pp.promotion.startDate AND pp.promotion.endDate
""")
    Optional<Promotions> findActivePromotionByProduct(@Param("productId") Long productId,
                                                      @Param("now") LocalDateTime now);



}
