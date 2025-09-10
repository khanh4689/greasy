package com.gearsy.gearsy.repository;

import com.gearsy.gearsy.entity.Favorites;
import com.gearsy.gearsy.entity.Products;
import com.gearsy.gearsy.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FavoriteRepository extends JpaRepository<Favorites, Long> {
    List<Favorites> findByUser(Users user);
    boolean existsByUserAndProduct(Users user, Products product);
    Favorites findByUserAndProduct(Users user, Products product);
}
