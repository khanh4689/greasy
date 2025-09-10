// src/main/java/com/gearsy/gearsy/service/PromotionService.java
package com.gearsy.gearsy.service;

import com.gearsy.gearsy.dto.PromotionAdminDTO;
import com.gearsy.gearsy.entity.Promotions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PromotionService {
    Page<Promotions> getAllPromotionsPaged(Pageable pageable);
    PromotionAdminDTO getPromotionAdminDTOById(Long promotionId);
    void addPromotion(PromotionAdminDTO promotionDTO);
    void updatePromotion(PromotionAdminDTO promotionDTO);
    void deletePromotion(Long promotionId);
    void updatePromotionStatus(Long promotionId, String status);
    Page<Promotions> searchPromotions(String keyword, int page, int size);
}