package com.gearsy.gearsy.controller.admin;

import com.gearsy.gearsy.entity.Categories;
import com.gearsy.gearsy.service.CategoriesService;
import com.gearsy.gearsy.service.SupplierService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
public class AdminCategoriesController {

    private final CategoriesService categoriesService;
    private final SupplierService suppliersService;

    @GetMapping
    public String listCategories(@RequestParam(required = false) String keyword,
                                 @PageableDefault(size = 10) Pageable pageable,
                                 Model model) {
        try {
            Page<Categories> categoriesPage = (keyword != null && !keyword.isEmpty())
                    ? categoriesService.searchCategoriesByName(keyword, pageable)
                    : categoriesService.getAllCategories(pageable);

            model.addAttribute("categoriesPage", categoriesPage);
            model.addAttribute("keyword", keyword);
            model.addAttribute("currentPage", pageable.getPageNumber());
            model.addAttribute("totalPages", categoriesPage.getTotalPages());
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi khi tải danh sách danh mục: " + e.getMessage());
        }
        return "admin/category/list";
    }

    @GetMapping("/add")
    public String addCategoryForm(Model model) {
        model.addAttribute("category", new Categories());
        model.addAttribute("suppliers", suppliersService.getAllSuppliers());
        return "admin/category/form";
    }

    @GetMapping("/edit/{id}")
    public String editCategoryForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Categories category = categoriesService.getCategoryById(id);
            model.addAttribute("category", category);
            model.addAttribute("suppliers", suppliersService.getAllSuppliers());
            return "admin/category/form";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/categories";
        }
    }

    @PostMapping("/save")
    public String saveCategory(@ModelAttribute("category") Categories category,
                               @RequestParam("imageFile") MultipartFile file,
                               RedirectAttributes redirect,
                               Model model) {
        try {
            if (!file.isEmpty()) {
                String filename = StringUtils.cleanPath(file.getOriginalFilename());

                // Tạo thư mục nếu chưa tồn tại
                String uploadDir = new File("src/main/resources/static/images").getAbsolutePath();
                File uploadPath = new File(uploadDir);
                if (!uploadPath.exists()) uploadPath.mkdirs();

                // Lưu file vào thư mục
                Path filePath = Paths.get(uploadDir, filename);
                file.transferTo(filePath);

                // Gán tên ảnh vào entity
                category.setImages(filename);
            } else if (category.getCategoryId() != null) {
                // Nếu không upload ảnh mới → giữ nguyên ảnh cũ
                String existingImage = categoriesService.getCategoryById(category.getCategoryId()).getImages();
                category.setImages(existingImage);
            }

            // Lưu danh mục
            categoriesService.saveCategory(category);
            redirect.addFlashAttribute("message", "Lưu danh mục thành công!");
            return "redirect:/admin/categories";

        } catch (IOException e) {
            model.addAttribute("error", "Lỗi khi lưu ảnh: " + e.getMessage());
            model.addAttribute("suppliers", suppliersService.getAllSuppliers());
            return "admin/category/form";
        }
    }

    @GetMapping("/view/{id}")
    public String viewCategory(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute("category", categoriesService.getCategoryById(id));
            return "admin/category/view";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/categories";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteCategory(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            categoriesService.deleteCategory(id);
            redirectAttributes.addFlashAttribute("message", "Xóa danh mục thành công!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/categories";
    }

    @GetMapping("/toggle-hidden/{id}")
    public String toggleHidden(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Categories category = categoriesService.getCategoryById(id);
            category.setHidden(!category.isHidden());
            categoriesService.saveCategory(category);

            String status = category.isHidden() ? "đã được ẩn" : "đã hiển thị lại";
            redirectAttributes.addFlashAttribute("message", "Danh mục \"" + category.getName() + "\" " + status + "!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", "Không thể thay đổi trạng thái: " + e.getMessage());
        }
        return "redirect:/admin/categories";
    }

}
