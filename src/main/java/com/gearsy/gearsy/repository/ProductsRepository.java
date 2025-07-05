package com.gearsy.gearsy.repository;

import com.gearsy.gearsy.entity.Products;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;


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
    WHERE p.hidden = FALSE AND p.stock > 1
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
    @Query("""
    SELECT p FROM Products p
    WHERE p.hidden = false
      AND p.category.categoryId = :categoryId
      AND (:minPrice IS NULL OR p.price >= :minPrice)
      AND (:maxPrice IS NULL OR p.price <= :maxPrice)
      AND (:supplierIds IS NULL OR p.supplier.supplierId IN :supplierIds)
""")
    Page<Products> filterProducts(@Param("categoryId") Long categoryId,
                                  @Param("minPrice") BigDecimal minPrice,
                                  @Param("maxPrice") BigDecimal maxPrice,
                                  @Param("supplierIds") List<Long> supplierIds,
                                  Pageable pageable);

    @Query("""
    SELECT p FROM Products p
    WHERE p.hidden = false
      AND p.category.categoryId = :categoryId
      AND (:minPrice IS NULL OR p.price >= :minPrice)
      AND (:maxPrice IS NULL OR p.price <= :maxPrice)
      AND (:supplierIds IS NULL OR p.supplier.supplierId IN :supplierIds)
    ORDER BY p.price ASC
""")
    Page<Products> filterProductsOrderByPriceAsc(@Param("categoryId") Long categoryId,
                                                 @Param("minPrice") BigDecimal minPrice,
                                                 @Param("maxPrice") BigDecimal maxPrice,
                                                 @Param("supplierIds") List<Long> supplierIds,
                                                 Pageable pageable);


    @Query("""
    SELECT p FROM Products p
    WHERE p.hidden = false
      AND p.category.categoryId = :categoryId
      AND (:minPrice IS NULL OR p.price >= :minPrice)
      AND (:maxPrice IS NULL OR p.price <= :maxPrice)
      AND (:supplierIds IS NULL OR p.supplier.supplierId IN :supplierIds)
    ORDER BY p.price DESC
""")
    Page<Products> filterProductsOrderByPriceDesc(@Param("categoryId") Long categoryId,
                                                  @Param("minPrice") BigDecimal minPrice,
                                                  @Param("maxPrice") BigDecimal maxPrice,
                                                  @Param("supplierIds") List<Long> supplierIds,
                                                  Pageable pageable);
    @Query("""
    SELECT p FROM Products p
    WHERE p.hidden = false AND p.category.categoryId = :categoryId
    ORDER BY p.createdAt DESC
""")
    Page<Products> findNewestInCategory(@Param("categoryId") Long categoryId, Pageable pageable);

    @Query("""
    SELECT p FROM Products p
    WHERE p.hidden = false AND p.category.categoryId = :categoryId
    ORDER BY p.price ASC
""")
    Page<Products> findCheapestInCategory(@Param("categoryId") Long categoryId, Pageable pageable);

    @Query("""
    SELECT p FROM Products p
    WHERE p.hidden = false AND p.category.categoryId = :categoryId
    ORDER BY p.price DESC
""")
    Page<Products> findMostExpensiveInCategory(@Param("categoryId") Long categoryId, Pageable pageable);

    @Query("""
    SELECT p FROM Products p
    WHERE p.hidden = false AND 
          (LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) 
          OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')))
    ORDER BY p.createdAt DESC
""")
    Page<Products> searchByKeywordOrderByCreatedAtDesc(@Param("keyword") String keyword, Pageable pageable);

    @Query("""
    SELECT p FROM Products p
    WHERE p.hidden = false AND 
          (LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) 
          OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')))
    ORDER BY p.price ASC
""")
    Page<Products> searchByKeywordOrderByPriceAsc(@Param("keyword") String keyword, Pageable pageable);

    @Query("""
    SELECT p FROM Products p
    WHERE p.hidden = false AND 
          (LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) 
          OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')))
    ORDER BY p.price DESC
""")
    Page<Products> searchByKeywordOrderByPriceDesc(@Param("keyword") String keyword, Pageable pageable);

    @Query("""
    SELECT DISTINCT p FROM Products p
    LEFT JOIN FETCH p.productPromotions pp
    LEFT JOIN FETCH pp.promotion promo
    WHERE p.productId = :productId
      AND (promo.status = 'ACTIVE' OR promo IS NULL)
""")
    Optional<Products> findProductWithActivePromotionById(@Param("productId") Long productId);


}
