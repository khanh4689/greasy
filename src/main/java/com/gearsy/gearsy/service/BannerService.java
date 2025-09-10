package com.gearsy.gearsy.service;

import com.gearsy.gearsy.entity.Banners;
import com.gearsy.gearsy.repository.BannerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BannerService {
    private final BannerRepository bannerRepository;

    public List<Banners> getAllBanners() {
        return bannerRepository.findAll();
    }
}