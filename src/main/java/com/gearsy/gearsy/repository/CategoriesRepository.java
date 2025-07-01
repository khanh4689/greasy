package com.gearsy.gearsy.repository;

import com.gearsy.gearsy.dto.DiscountedCategoryDTO;
import com.gearsy.gearsy.entity.Categories;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CategoriesRepository extends JpaRepository<Categories, Long> {
    @Query("""
                SELECT new com.gearsy.gearsy.dto.DiscountedCategoryDTO(
                    c.categoryId, c.name, MAX(pr.discountPercent), MAX(pr.endDate), c.images
                )
                FROM Categories c
                JOIN Products p ON p.category = c
                JOIN ProductPromotions pp ON pp.product = p
                JOIN Promotions pr ON pp.promotion = pr
                WHERE pr.status = 'Active'
                  AND CURRENT_DATE BETWEEN pr.startDate AND pr.endDate
                GROUP BY c.categoryId, c.name, c.images
            """)
    List<DiscountedCategoryDTO> findCategoriesWithDiscountToday();

    // Phân trang + tìm kiếm theo tên danh mục
    Page<Categories> findByNameContainingIgnoreCase(String name, Pageable pageable);
}


