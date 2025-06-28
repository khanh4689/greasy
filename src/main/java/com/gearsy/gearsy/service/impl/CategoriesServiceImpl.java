package com.gearsy.gearsy.service.impl;
import com.gearsy.gearsy.dto.DiscountedCategoryDTO;
import com.gearsy.gearsy.entity.Categories;
import com.gearsy.gearsy.repository.CategoriesRepository;
import com.gearsy.gearsy.service.CategoriesService;
import jdk.jfr.Category;
import lombok.RequiredArgsConstructor;
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
}
