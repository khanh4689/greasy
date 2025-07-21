package com.gearsy.gearsy.controller;

import com.gearsy.gearsy.dto.ProductDTO;
import com.gearsy.gearsy.entity.Products;
import com.gearsy.gearsy.entity.Reviews;
import com.gearsy.gearsy.entity.Suppliers;
import com.gearsy.gearsy.repository.ProductsRepository;
import com.gearsy.gearsy.repository.SupplierRepository;
import com.gearsy.gearsy.service.CategoriesService;
import com.gearsy.gearsy.service.ProductsService;
import com.gearsy.gearsy.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController {

    private final ProductsService productService;
    private final CategoriesService categoryService;
    private final SupplierRepository supplierRepository;
    private final ReviewService reviewService;

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    @GetMapping("/list")
    public String listProducts(@RequestParam(defaultValue = "0") int featuredPage,
                               @RequestParam(defaultValue = "0") int newestPage,
                               @RequestParam(defaultValue = "4") int size,
                               Model model) {
        Page<ProductDTO> featured = productService.getFeaturedProductsPaged(PageRequest.of(featuredPage, size));
        Page<ProductDTO> newest = productService.getNewestProductsPaged(PageRequest.of(newestPage, size));

        model.addAttribute("featuredProducts", featured.getContent());
        model.addAttribute("featuredTotalPages", featured.getTotalPages());
        model.addAttribute("featuredCurrentPage", featuredPage);
        model.addAttribute("newestProducts", newest.getContent());
        model.addAttribute("newestTotalPages", newest.getTotalPages());
        model.addAttribute("newestCurrentPage", newestPage);
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("discountedCategories", categoryService.getDiscountedCategoriesToday());
        model.addAttribute("contentTemplate", "product/views");
        return "layout";
    }

    @GetMapping("/search")
    public String searchProducts(@RequestParam(required = false) String keyword,
                                 @RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "8") int size,
                                 @RequestParam(defaultValue = "newest") String sortBy,
                                 Model model) {
        if (keyword == null || keyword.trim().isEmpty()) {
            model.addAttribute("products", List.of());
            model.addAttribute("message", "Vui lòng nhập từ khóa tìm kiếm.");
            model.addAttribute("contentTemplate", "product/search-result");
            return "layout";
        }

        Page<ProductDTO> resultPage = productService.getSortedProductsByKeyword(keyword, sortBy, PageRequest.of(page, size));

        model.addAttribute("products", resultPage.getContent());
        model.addAttribute("totalPages", resultPage.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("keyword", keyword);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("contentTemplate", "product/search-result");
        return "layout";
    }

    @GetMapping("/category/{id}")
    public String productsByCategory(@PathVariable("id") Long categoryId,
                                     @RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "8") int size,
                                     @RequestParam(required = false) BigDecimal minPrice,
                                     @RequestParam(required = false) BigDecimal maxPrice,
                                     @RequestParam(required = false) List<Long> supplierIds,
                                     @RequestParam(defaultValue = "newest") String sortBy,
                                     Model model) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductDTO> resultPage;

        boolean hasFilter = minPrice != null || maxPrice != null || (supplierIds != null && !supplierIds.isEmpty());

        if (hasFilter) {
            switch (sortBy) {
                case "cheapest":
                    resultPage = productService.filterProductsSortByPriceAsc(categoryId, minPrice, maxPrice, supplierIds, pageable);
                    break;
                case "expensive":
                    resultPage = productService.filterProductsSortByPriceDesc(categoryId, minPrice, maxPrice, supplierIds, pageable);
                    break;
                default:
                    resultPage = productService.filterProductsByCategory(categoryId, minPrice, maxPrice, supplierIds, pageable);
            }
        } else {
            resultPage = productService.getSortedProductsByCategory(categoryId, sortBy, pageable);
        }

        List<Suppliers> allSuppliers = supplierRepository.findSuppliersByCategory(categoryId);

        model.addAttribute("products", resultPage.getContent());
        model.addAttribute("totalPages", resultPage.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("selectedSuppliers", supplierIds);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("allSuppliers", allSuppliers);
        model.addAttribute("contentTemplate", "product/category-result");
        return "layout";
    }

    @GetMapping("/detail/{id}")
    public String productDetail(@PathVariable("id") Long productId,
                                @RequestParam(required = false) Long editingReviewId,
                                Model model,
                                Principal principal) {
        Products product = productService.getProductById(productId);
        List<Reviews> reviews = reviewService.getReviewsByProductId(productId);

        model.addAttribute("product", product);
        model.addAttribute("reviews", reviews);
        model.addAttribute("editingReviewId", editingReviewId);

        if (principal != null) {
            model.addAttribute("userEmail", principal.getName());
        }

        model.addAttribute("contentTemplate", "product/detail");
        return "layout";
    }

    @GetMapping("/review/edit")
    public String showEditReview(@RequestParam Long reviewId,
                                 @RequestParam Long productId,
                                 Principal principal,
                                 Model model) {
        if (principal == null) return "redirect:/auth/login";

        Products product = productService.getProductById(productId);
        List<Reviews> reviews = reviewService.getReviewsByProductId(productId);

        Reviews review = reviewService.getReviewByIdAndUserEmail(reviewId, principal.getName())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đánh giá"));

        model.addAttribute("product", product);
        model.addAttribute("reviews", reviews);
        model.addAttribute("editingReview", review);
        model.addAttribute("userEmail", principal.getName());
        model.addAttribute("contentTemplate", "product/detail");
        return "layout";
    }

    @PostMapping("/review")
    public String submitReview(@RequestParam Long productId,
                               @RequestParam int rating,
                               @RequestParam String comment,
                               Principal principal) {
        if (principal == null) return "redirect:/auth/login";
        reviewService.submitReview(productId, rating, comment, principal.getName());
        return "redirect:/products/detail/" + productId;
    }

    @PostMapping("/review/edit")
    public String editReview(@RequestParam Long reviewId,
                             @RequestParam int rating,
                             @RequestParam String comment,
                             @RequestParam Long productId,
                             Principal principal) {
        if (principal == null) return "redirect:/auth/login";
        reviewService.updateReview(reviewId, rating, comment, principal.getName());
        return "redirect:/products/detail/" + productId + "#rv-" + reviewId;
    }

    @PostMapping("/review/delete")
    public String deleteReview(@RequestParam Long reviewId,
                               @RequestParam Long productId,
                               Principal principal) {
        if (principal == null) return "redirect:/auth/login";
        reviewService.deleteReviewByEmail(reviewId, principal.getName());
        return "redirect:/products/detail/" + productId;
    }
}
