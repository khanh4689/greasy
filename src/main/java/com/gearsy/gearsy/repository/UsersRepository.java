package com.gearsy.gearsy.repository;

import com.gearsy.gearsy.entity.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UsersRepository extends JpaRepository<Users, Long> {

    // Tìm người dùng theo email
    Optional<Users> findByEmail(String email);

    // Kiểm tra email đã tồn tại hay chưa
    Boolean existsByEmail(String email);

    // Đếm số người dùng đang hoạt động
    @Query("SELECT COUNT(u) FROM Users u WHERE u.status = com.gearsy.gearsy.entity.UserStatus.Active")
    Long countActiveUsers();

    // Lấy danh sách email của tất cả người dùng
    @Query("SELECT u.email FROM Users u WHERE u.email IS NOT NULL")
    List<String> findAllEmails();

    // Tìm kiếm người dùng theo tên hoặc email (không phân biệt hoa thường)
    @Query("""
        SELECT u FROM Users u
        WHERE LOWER(u.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%'))
    """)
    Page<Users> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    // Đếm số người dùng đăng ký theo từng tháng của năm được chỉ định
    @Query("""
        SELECT MONTH(u.createdAt), COUNT(u)
        FROM Users u
        WHERE YEAR(u.createdAt) = :year
        GROUP BY MONTH(u.createdAt)
        ORDER BY MONTH(u.createdAt)
    """)
    List<Object[]> countRegisteredUsersByMonth(@Param("year") int year);
}
