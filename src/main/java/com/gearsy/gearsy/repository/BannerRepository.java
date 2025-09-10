package com.gearsy.gearsy.repository;

import com.gearsy.gearsy.entity.Banners;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BannerRepository extends JpaRepository<Banners, Long> {
    List<Banners> findAll();
}