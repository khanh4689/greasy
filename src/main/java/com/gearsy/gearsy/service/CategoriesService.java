package com.gearsy.gearsy.service;

import com.gearsy.gearsy.dto.DiscountedCategoryDTO;
import com.gearsy.gearsy.entity.Categories;


import java.util.List;

public interface CategoriesService {
    List<Categories> getAllCategories();
    List<DiscountedCategoryDTO> getDiscountedCategoriesToday();


}
