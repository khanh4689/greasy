package com.gearsy.gearsy.repository;

import com.gearsy.gearsy.entity.Carts;
import com.gearsy.gearsy.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Carts, Long> {
    Optional<Carts> findByUser(Users user);
}

