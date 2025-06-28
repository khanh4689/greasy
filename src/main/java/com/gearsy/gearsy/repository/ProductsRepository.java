package com.gearsy.gearsy.repository;

import com.gearsy.gearsy.entity.Products;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ProductsRepository extends JpaRepository<Products, Long> {
    Page<Products> findByCategory_CategoryId(Long categoryId, Pageable pageable);

    @Query("""
    SELECT p FROM Products p
    WHERE p.hidden = FALSE
    ORDER BY p.createdAt DESC
""")
    Page<Products> findNewestProducts(Pageable pageable);

    @Query("""
    SELECT p FROM Products p
    WHERE p.hidden = FALSE AND p.stock > 5
    ORDER BY p.stock DESC
""")
    Page<Products> findFeaturedProducts(Pageable pageable);

    Page<Products> findByHiddenFalseAndStockGreaterThan(int stock, Pageable pageable);

    Page<Products> findByHiddenFalseOrderByCreatedAtDesc(Pageable pageable);

    // ✅ Sửa thành trả về Page và có điều kiện hidden = false
    @Query("""
        SELECT p FROM Products p 
        WHERE p.hidden = false AND (
            LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) 
            OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
        )
    """)
    Page<Products> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    // ✅ Thêm điều kiện hidden = false
    Page<Products> findByCategory_CategoryIdAndHiddenFalse(Long categoryId, Pageable pageable);






}
