package com.gearsy.gearsy.service.impl;

import com.gearsy.gearsy.dto.PromotionAdminDTO;
import com.gearsy.gearsy.entity.Promotions;
import com.gearsy.gearsy.repository.PromotionRepository;
import com.gearsy.gearsy.service.PromotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PromotionServiceImpl implements PromotionService {

    private final PromotionRepository promotionRepository;

    private static final List<String> VALID_STATUSES = Arrays.asList("Active", "Inactive", "Scheduled");

    @Override
    public Page<Promotions> getAllPromotionsPaged(Pageable pageable) {
        return promotionRepository.findAll(pageable);
    }

    @Override
    public PromotionAdminDTO getPromotionAdminDTOById(Long promotionId) {
        Promotions promotion = promotionRepository.findById(promotionId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khuyến mãi với ID: " + promotionId));
        return toPromotionAdminDTO(promotion);
    }

    @Override
    public void addPromotion(PromotionAdminDTO promotionDTO) {
        validatePromotionDTO(promotionDTO);
        Promotions promotion = new Promotions();
        mapDTOToPromotion(promotionDTO, promotion);
        promotionRepository.save(promotion);
    }

    @Override
    public void updatePromotion(PromotionAdminDTO promotionDTO) {
        validatePromotionDTO(promotionDTO);
        Promotions promotion = promotionRepository.findById(promotionDTO.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khuyến mãi với ID: " + promotionDTO.getId()));
        mapDTOToPromotion(promotionDTO, promotion);
        promotionRepository.save(promotion);
    }

    @Override
    public void deletePromotion(Long promotionId) {
        Promotions promotion = promotionRepository.findById(promotionId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khuyến mãi với ID: " + promotionId));
        promotionRepository.delete(promotion);
    }

    @Override
    public void updatePromotionStatus(Long promotionId, String status) {
        if (!VALID_STATUSES.contains(status)) {
            throw new IllegalArgumentException("Trạng thái không hợp lệ: " + status);
        }
        Promotions promotion = promotionRepository.findById(promotionId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khuyến mãi với ID: " + promotionId));
        promotion.setStatus(status);
        promotionRepository.save(promotion);
    }

    // --- Helper methods for DTO mapping ---
    private PromotionAdminDTO toPromotionAdminDTO(Promotions promotion) {
        PromotionAdminDTO dto = new PromotionAdminDTO();
        dto.setId(promotion.getPromotionId());
        dto.setName(promotion.getName());
        dto.setDiscountPercent(promotion.getDiscountPercent());
        dto.setStartDate(promotion.getStartDate());
        dto.setEndDate(promotion.getEndDate());
        dto.setStatus(promotion.getStatus());
        return dto;
    }

    private void mapDTOToPromotion(PromotionAdminDTO dto, Promotions promotion) {
        promotion.setName(dto.getName());
        promotion.setDiscountPercent(dto.getDiscountPercent());
        promotion.setStartDate(dto.getStartDate());
        promotion.setEndDate(dto.getEndDate());
        promotion.setStatus(dto.getStatus());
    }

    private void validatePromotionDTO(PromotionAdminDTO dto) {
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên khuyến mãi không được để trống.");
        }
        if (dto.getDiscountPercent() == null || dto.getDiscountPercent().compareTo(BigDecimal.ZERO) < 0 || dto.getDiscountPercent().compareTo(new BigDecimal("100")) > 0) {
            throw new IllegalArgumentException("Phần trăm giảm giá phải từ 0 đến 100.");
        }
        if (dto.getStartDate() == null || dto.getEndDate() == null) {
            throw new IllegalArgumentException("Ngày bắt đầu và ngày kết thúc không được để trống.");
        }
        if (dto.getStartDate().isAfter(dto.getEndDate())) {
            throw new IllegalArgumentException("Ngày kết thúc phải sau ngày bắt đầu.");
        }
        if (dto.getStatus() == null || !VALID_STATUSES.contains(dto.getStatus())) {
            throw new IllegalArgumentException("Trạng thái không hợp lệ: " + dto.getStatus());
        }
    }

    @Override
    public Page<Promotions> searchPromotions(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("startDate").descending());
        return promotionRepository.searchByKeyword(keyword, pageable);
    }
}