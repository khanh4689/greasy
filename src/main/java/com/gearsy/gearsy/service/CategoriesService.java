package com.gearsy.gearsy.service;

import com.gearsy.gearsy.dto.DiscountedCategoryDTO;
import com.gearsy.gearsy.entity.Categories;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import java.util.List;

public interface CategoriesService {
    List<Categories> getAllCategories();
    List<DiscountedCategoryDTO> getDiscountedCategoriesToday();

    Page<Categories> getAllCategories(Pageable pageable);
    Page<Categories> searchCategoriesByName(String name, Pageable pageable);


    Categories getCategoryById(Long id);

    void saveCategory(Categories category);

    void deleteCategory(Long id);
    List<Categories> getVisibleCategories();
}
