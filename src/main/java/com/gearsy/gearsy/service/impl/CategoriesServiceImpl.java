package com.gearsy.gearsy.service.impl;
import com.gearsy.gearsy.dto.DiscountedCategoryDTO;
import com.gearsy.gearsy.entity.Categories;
import com.gearsy.gearsy.repository.CategoriesRepository;
import com.gearsy.gearsy.service.CategoriesService;
import jdk.jfr.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoriesServiceImpl implements CategoriesService {

    private final CategoriesRepository categoryRepository;


    @Override
    public List<Categories> getAllCategories() {
        return categoryRepository.findAll();

    }

    @Override
    public List<DiscountedCategoryDTO> getDiscountedCategoriesToday() {
        return categoryRepository.findCategoriesWithDiscountToday();
    }

    // Mới: phân trang tất cả danh mục
    @Override
    public Page<Categories> getAllCategories(Pageable pageable) {
        return categoryRepository.findAll(pageable);
    }

    // Mới: tìm kiếm theo tên danh mục + phân trang
    @Override
    public Page<Categories> searchCategoriesByName(String name, Pageable pageable) {
        return categoryRepository.findByNameContainingIgnoreCase(name, pageable);
    }

    @Override
    public Categories getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục với ID: " + id));
    }

    @Override
    public void saveCategory(Categories category) {
        categoryRepository.save(category);
    }

    @Override
    public void deleteCategory(Long id) {
        // Kiểm tra xem danh mục có tồn tại không
        if (!categoryRepository.existsById(id)) {
            throw new RuntimeException("Danh mục với ID " + id + " không tồn tại");
        }
        try {
            categoryRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new RuntimeException("Không thể xóa danh mục vì còn sản phẩm liên quan");
        }
    }

    @Override
    public List<Categories> getVisibleCategories() {
        return categoryRepository.findByHiddenFalse();
    }

}
