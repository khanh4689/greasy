package com.gearsy.gearsy.service;

import com.gearsy.gearsy.dto.ProductAdminDTO;
import com.gearsy.gearsy.dto.ProductDTO;
import com.gearsy.gearsy.entity.Products;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public interface ProductsService {
    Page<Products> findAll(Pageable pageable);
    Page<Products> findByCategoryId(Long categoryId, Pageable pageable);

    List<ProductDTO> getNewestProducts();
    List<ProductDTO> getFeaturedProducts();

    Page<ProductDTO> getFeaturedProductsPaged(Pageable pageable);
    Page<ProductDTO> getNewestProductsPaged(Pageable pageable);


    Page<ProductDTO> getProductsByCategory(Long categoryId, Pageable pageable);
    Page<ProductDTO> filterProductsByCategory(Long categoryId, BigDecimal minPrice,
                                              BigDecimal maxPrice, List<Long> supplierIds,
                                              Pageable pageable);
    Page<ProductDTO> filterProductsSortByPriceAsc(Long categoryId, BigDecimal minPrice,
                                                  BigDecimal maxPrice, List<Long> supplierIds,
                                                  Pageable pageable);

    Page<ProductDTO> filterProductsSortByPriceDesc(Long categoryId, BigDecimal minPrice,
                                                   BigDecimal maxPrice, List<Long> supplierIds,
                                                   Pageable pageable);

    Page<ProductDTO> getSortedProductsByCategory(Long categoryId, String sortType, Pageable pageable);

    Page<ProductDTO> getSortedProductsByKeyword(String keyword, String sortType, Pageable pageable);

    Products getProductById(Long productId);

    void addProduct(ProductAdminDTO productDTO);

    void updateProduct(ProductAdminDTO productDTO);

    void hideProduct(Long productId);

    void unhideProduct(Long productId);

    void updateStock(Long productId, Integer stock);

    ProductAdminDTO getProductAdminDTOById(Long productId);

    Page<ProductAdminDTO> getAllProductsPaged(Pageable pageable);

    void deleteProduct(Long productId);
    Page<ProductAdminDTO> getAllProductsPaged(String keyword, Pageable pageable);



}