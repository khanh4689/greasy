package com.gearsy.gearsy.controller;

import com.gearsy.gearsy.entity.Categories;
import com.gearsy.gearsy.service.CategoriesService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/user/categories")
@RequiredArgsConstructor
public class CategoriesController {

    private final CategoriesService categoriesService;

    @GetMapping
    public String listCategories(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            Model model) {

        // Giới hạn size để tránh giá trị không hợp lệ
        size = Math.max(1, Math.min(size, 100));

        Page<Categories> categoriesPage;
        if (keyword == null || keyword.trim().isEmpty()) {
            categoriesPage = categoriesService.getAllCategories(PageRequest.of(page, size));
        } else {
            categoriesPage = categoriesService.searchCategoriesByName(keyword.trim(), PageRequest.of(page, size));
        }

        // Thêm thông báo nếu không có kết quả
        if (categoriesPage.isEmpty()) {
            model.addAttribute("message", "Không tìm thấy danh mục nào.");
        }

        model.addAttribute("categoriesPage", categoriesPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", categoriesPage.getTotalPages());
        model.addAttribute("keyword", keyword);

        return "categories/list";
    }
}