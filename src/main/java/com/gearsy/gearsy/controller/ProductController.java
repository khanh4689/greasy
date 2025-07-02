    package com.gearsy.gearsy.controller;

    import com.gearsy.gearsy.dto.ProductDTO;
    import com.gearsy.gearsy.entity.Products;
    import com.gearsy.gearsy.entity.Suppliers;
    import com.gearsy.gearsy.repository.ProductsRepository;
    import com.gearsy.gearsy.repository.SupplierRepository;
    import com.gearsy.gearsy.service.CategoriesService;
    import com.gearsy.gearsy.service.ProductsService;
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
    import java.util.List;
    import java.util.stream.Collectors;

    @Controller
    @RequiredArgsConstructor
    @RequestMapping("/products")
    public class ProductController {

        private final ProductsService productService;
        private final CategoriesService categoryService;
        private final SupplierRepository supplierRepository;

        private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

        // ✅ Hiển thị sản phẩm nổi bật + mới nhất
        @GetMapping("/list")
        public String listProducts(
                @RequestParam(defaultValue = "0") int featuredPage,
                @RequestParam(defaultValue = "0") int newestPage,
                @RequestParam(defaultValue = "4") int size,
                Model model
        ) {
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


        // Tìm kiếm sản phẩm theo từ khóa
        @GetMapping("/search")
        public String searchProducts(@RequestParam(required = false) String keyword,
                                     @RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "8") int size,
                                     @RequestParam(required = false, defaultValue = "newest") String sortBy,
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
            model.addAttribute("sortBy", sortBy); // để dùng trên giao diện
            model.addAttribute("contentTemplate", "product/search-result");

            return "layout";
        }



        // Lọc sản phẩm theo danh mục
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
                // Trường hợp có lọc giá hoặc supplier
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
                // Không lọc giá/supplier, chỉ sort đơn giản
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





    }
