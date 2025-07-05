package com.gearsy.gearsy.service.impl;

import com.gearsy.gearsy.dto.ProductDTO;
import com.gearsy.gearsy.entity.Products;
import com.gearsy.gearsy.entity.Promotions;
import com.gearsy.gearsy.repository.ProductsRepository;

import com.gearsy.gearsy.repository.PromotionRepository;
import com.gearsy.gearsy.service.ProductsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductsServiceImpl implements ProductsService {
    private final ProductsRepository productRepository;
    private final PromotionRepository promotionRepository;

    @Override
    public Page<Products> findAll(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    @Override
    public Page<Products> findByCategoryId(Long categoryId, Pageable pageable) {
        return productRepository.findByCategory_CategoryId(categoryId, pageable);
    }

    @Override
    public List<ProductDTO> getNewestProducts() {
        Pageable pageable = Pageable.ofSize(8); // hoặc PageRequest.of(0, 8)
        return productRepository.findNewestProducts(pageable)
                .stream()
                .map(this::toProductDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductDTO> getFeaturedProducts() {
        Pageable pageable = Pageable.ofSize(8);
        return productRepository.findFeaturedProducts(pageable)
                .stream()
                .map(this::toProductDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Page<ProductDTO> getFeaturedProductsPaged(Pageable pageable) {
        Page<Products> page = productRepository.findByHiddenFalseAndStockGreaterThan(5, pageable);

        return page.map(this::toProductDTO);
    }

    @Override
    public Page<ProductDTO> getNewestProductsPaged(Pageable pageable) {
        Page<Products> page = productRepository.findByHiddenFalseOrderByCreatedAtDesc(pageable);
        return page.map(this::toProductDTO);
    }


    private ProductDTO toProductDTO(Products product) {
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getProductId());
        dto.setName(product.getName());
        dto.setImage(product.getImages());
        dto.setOriginalPrice(product.getPrice());
        dto.setCreatedAt(product.getCreatedAt());

        Optional<Promotions> promo = promotionRepository
                .findActivePromotionByProduct(product.getProductId(), LocalDateTime.now());

        if (promo.isPresent()) {
            Promotions p = promo.get();
            BigDecimal discount = p.getDiscountPercent();
            BigDecimal discountedPrice = product.getPrice()
                    .subtract(product.getPrice().multiply(discount).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP));

            dto.setDiscountedPrice(discountedPrice);
            dto.setOnSale(true);

            // ✅ LOG chi tiết khuyến mãi
            System.out.println(">>>>> Product: " + product.getName());
            System.out.println(">>>>> Promotion found: " + discount + "%");
            System.out.println(">>>>> StartDate: " + p.getStartDate());
            System.out.println(">>>>> EndDate:   " + p.getEndDate());
            System.out.println(">>>>> NOW:       " + LocalDateTime.now());
            System.out.println(">>>>> Discounted Price: " + discountedPrice);
            System.out.println(">>>>> isOnSale: true");
            System.out.println(">>>>> Checking product ID: " + product.getProductId());
        } else {
            dto.setOnSale(false);

            // ❗ Log nếu không có khuyến mãi
            System.out.println(">>>>> Product: " + product.getName());
            System.out.println(">>>>> No active promotion");
            System.out.println(">>>>> isOnSale: false");
            System.out.println(">>> NOW: " + LocalDateTime.now());
        }

        return dto;
    }


    @Override
    public Page<ProductDTO> searchProducts(String keyword, Pageable pageable) {
        Page<Products> products = productRepository.searchByKeyword(keyword, pageable);
        return products.map(this::toProductDTO);
    }

    @Override
    public Page<ProductDTO> getProductsByCategory(Long categoryId, Pageable pageable) {
        Page<Products> products = productRepository.findByCategory_CategoryIdAndHiddenFalse(categoryId, pageable);
        return products.map(this::toProductDTO);
    }

    @Override
    public Page<ProductDTO> filterProductsByCategory(Long categoryId, BigDecimal minPrice,
                                                     BigDecimal maxPrice, List<Long> supplierIds,
                                                     Pageable pageable) {
        Page<Products> products = productRepository.filterProducts(categoryId, minPrice, maxPrice, supplierIds, pageable);
        return products.map(this::toProductDTO);
    }

    @Override
    public Page<ProductDTO> filterProductsSortByPriceAsc(Long categoryId, BigDecimal minPrice,
                                                         BigDecimal maxPrice, List<Long> supplierIds,
                                                         Pageable pageable) {
        Page<Products> products = productRepository.filterProductsOrderByPriceAsc(categoryId, minPrice, maxPrice, supplierIds, pageable);
        return products.map(this::toProductDTO);
    }

    @Override
    public Page<ProductDTO> filterProductsSortByPriceDesc(Long categoryId, BigDecimal minPrice,
                                                          BigDecimal maxPrice, List<Long> supplierIds,
                                                          Pageable pageable) {
        Page<Products> products = productRepository.filterProductsOrderByPriceDesc(categoryId, minPrice, maxPrice, supplierIds, pageable);
        return products.map(this::toProductDTO);
    }

    @Override
    public Page<ProductDTO> getSortedProductsByCategory(Long categoryId, String sortType, Pageable pageable) {
        Page<Products> products;

        switch (sortType) {
            case "cheapest":
                products = productRepository.findCheapestInCategory(categoryId, pageable);
                break;
            case "expensive":
                products = productRepository.findMostExpensiveInCategory(categoryId, pageable);
                break;
            case "newest":
            default:
                products = productRepository.findNewestInCategory(categoryId, pageable);
                break;
        }

        return products.map(this::toProductDTO);
    }

    @Override
    public Page<ProductDTO> getSortedProductsByKeyword(String keyword, String sortType, Pageable pageable) {
        Page<Products> products;

        switch (sortType) {
            case "cheapest":
                products = productRepository.searchByKeywordOrderByPriceAsc(keyword, pageable);
                break;
            case "expensive":
                products = productRepository.searchByKeywordOrderByPriceDesc(keyword, pageable);
                break;
            case "newest":
            default:
                products = productRepository.searchByKeywordOrderByCreatedAtDesc(keyword, pageable);
                break;
        }

        return products.map(this::toProductDTO);
    }

    @Override
    public Products getProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm với ID: " + productId));
    }










}

