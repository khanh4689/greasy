package com.gearsy.gearsy.controller.admin;

import com.gearsy.gearsy.dto.PromotionAdminDTO;
import com.gearsy.gearsy.entity.Promotions;
import com.gearsy.gearsy.service.PromotionService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/Promotions")
public class AdminPromotionController {

    private final PromotionService promotionService;

    private static final Logger logger = LoggerFactory.getLogger(AdminPromotionController.class);

    @GetMapping("/list")
    public String listPromotions(@RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "10") int size,
                                 Model model) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Promotions> promotionsPage = promotionService.getAllPromotionsPaged(pageable);

        model.addAttribute("promotions", promotionsPage.getContent());
        model.addAttribute("totalPages", promotionsPage.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("size", size);
        model.addAttribute("contentTemplate", "admin/Promotions/list");
        return "admin/Promotions/layout";
    }

    @GetMapping("/search")
    public String searchPromotions(
            @RequestParam("keyword") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        Page<Promotions> searchResult = promotionService.searchPromotions(keyword, page, size);

        model.addAttribute("promotions", searchResult.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", searchResult.getTotalPages());
        model.addAttribute("size", size);
        model.addAttribute("keyword", keyword); // để giữ lại trong form

        return "admin/promotions/list"; // hoặc tên file HTML bạn đang dùng
    }

    @GetMapping("/add")
    public String showAddPromotionForm(Model model) {
        model.addAttribute("promotion", new PromotionAdminDTO());
        model.addAttribute("contentTemplate", "admin/Promotions/add");
        return "admin/Promotions/layout";
    }

    @PostMapping("/add")
    public String addPromotion(@ModelAttribute("promotion") PromotionAdminDTO promotionDTO,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        try {
            promotionService.addPromotion(promotionDTO);
            logger.info("Đã thêm khuyến mãi thành công: {}", promotionDTO.getName());
            redirectAttributes.addFlashAttribute("success", "Thêm khuyến mãi thành công!");
            return "redirect:/admin/Promotions/list";
        } catch (IllegalArgumentException e) {
            logger.error("Lỗi khi thêm khuyến mãi: {}", e.getMessage(), e);
            model.addAttribute("error", e.getMessage());
            model.addAttribute("contentTemplate", "admin/Promotions/add");
            return "admin/Promotions/layout";
        } catch (Exception e) {
            logger.error("Lỗi không xác định khi thêm khuyến mãi: {}", e.getMessage(), e);
            model.addAttribute("error", "Không thể thêm khuyến mãi: Đã xảy ra lỗi không mong muốn.");
            model.addAttribute("contentTemplate", "admin/Promotions/add");
            return "admin/Promotions/layout";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditPromotionForm(@PathVariable("id") Long promotionId, Model model) {
        try {
            PromotionAdminDTO promotion = promotionService.getPromotionAdminDTOById(promotionId);
            model.addAttribute("promotion", promotion);
            model.addAttribute("contentTemplate", "admin/Promotions/edit");
            return "admin/Promotions/layout";
        } catch (RuntimeException e) {
            logger.error("Lỗi khi lấy khuyến mãi: {}", e.getMessage(), e);
            model.addAttribute("error", e.getMessage());
            model.addAttribute("contentTemplate", "admin/Promotions/list");
            return "admin/Promotions/layout";
        }
    }

    @PostMapping("/edit/{id}")
    public String editPromotion(@PathVariable("id") Long promotionId,
                                @ModelAttribute("promotion") PromotionAdminDTO promotionDTO,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        try {
            promotionDTO.setId(promotionId);
            promotionService.updatePromotion(promotionDTO);
            logger.info("Đã cập nhật khuyến mãi thành công: {}", promotionDTO.getName());
            redirectAttributes.addFlashAttribute("success", "Cập nhật khuyến mãi thành công!");
            return "redirect:/admin/Promotions/list";
        } catch (IllegalArgumentException e) {
            logger.error("Lỗi khi cập nhật khuyến mãi: {}", e.getMessage(), e);
            model.addAttribute("error", e.getMessage());
            model.addAttribute("contentTemplate", "admin/Promotions/edit");
            promotionDTO.setId(promotionId);
            model.addAttribute("promotion", promotionDTO);
            return "admin/Promotions/layout";
        } catch (Exception e) {
            logger.error("Lỗi không xác định khi cập nhật khuyến mãi: {}", e.getMessage(), e);
            model.addAttribute("error", "Không thể cập nhật khuyến mãi: Đã xảy ra lỗi không mong muốn.");
            model.addAttribute("contentTemplate", "admin/Promotions/edit");
            promotionDTO.setId(promotionId);
            model.addAttribute("promotion", promotionDTO);
            return "admin/Promotions/layout";
        }
    }

    @PostMapping("/delete/{id}")
    public String deletePromotion(@PathVariable("id") Long promotionId,
                                  @RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "10") int size,
                                  RedirectAttributes redirectAttributes) {
        try {
            promotionService.deletePromotion(promotionId);
            logger.info("Đã xóa khuyến mãi thành công: {}", promotionId);
            redirectAttributes.addFlashAttribute("success", "Xóa khuyến mãi thành công!");
            return "redirect:/admin/Promotions/list?page=" + page + "&size=" + size;
        } catch (Exception e) {
            logger.error("Lỗi khi xóa khuyến mãi: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "Không thể xóa khuyến mãi: " + e.getMessage());
            return "redirect:/admin/Promotions/list?page=" + page + "&size=" + size;
        }
    }

    @PostMapping("/update-status/{id}")
    public String updatePromotionStatus(@PathVariable("id") Long promotionId,
                                        @RequestParam("status") String status,
                                        RedirectAttributes redirectAttributes) {
        try {
            promotionService.updatePromotionStatus(promotionId, status);
            redirectAttributes.addFlashAttribute("success", "Cập nhật trạng thái khuyến mãi thành công!");
            return "redirect:/admin/Promotions/list";
        } catch (Exception e) {
            logger.error("Lỗi khi cập nhật trạng thái khuyến mãi: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "Không thể cập nhật trạng thái: " + e.getMessage());
            return "redirect:/admin/Promotions/list";
        }
    }
}