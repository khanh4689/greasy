package com.gearsy.gearsy.service;

import com.gearsy.gearsy.dto.ProductDTO;
import com.gearsy.gearsy.entity.Products;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ProductsService {
    Page<Products> findAll(Pageable pageable);
    Page<Products> findByCategoryId(Long categoryId, Pageable pageable);

    List<ProductDTO> getNewestProducts();
    List<ProductDTO> getFeaturedProducts();

    Page<ProductDTO> getFeaturedProductsPaged(Pageable pageable);
    Page<ProductDTO> getNewestProductsPaged(Pageable pageable);

    Page<ProductDTO> searchProducts(String keyword, Pageable pageable);
    Page<ProductDTO> getProductsByCategory(Long categoryId, Pageable pageable);


}
