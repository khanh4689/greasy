package com.gearsy.gearsy.repository;

import com.gearsy.gearsy.entity.Suppliers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupplierRepository extends JpaRepository<Suppliers, Long> {
    @Query("""
    SELECT DISTINCT p.supplier FROM Products p
    WHERE p.category.categoryId = :categoryId AND p.hidden = false
""")
    List<Suppliers> findSuppliersByCategory(@Param("categoryId") Long categoryId);
}
