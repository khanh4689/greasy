    package com.gearsy.gearsy.controller;

    import com.gearsy.gearsy.dto.ProductDTO;
    import com.gearsy.gearsy.entity.Products;
    import com.gearsy.gearsy.repository.ProductsRepository;
    import com.gearsy.gearsy.service.CategoriesService;
    import com.gearsy.gearsy.service.ProductsService;
    import lombok.RequiredArgsConstructor;
    import org.slf4j.Logger;
    import org.slf4j.LoggerFactory;
    import org.springframework.data.domain.Page;
    import org.springframework.data.domain.PageRequest;
    import org.springframework.stereotype.Controller;
    import org.springframework.ui.Model;
    import org.springframework.web.bind.annotation.*;

    import java.util.List;
    import java.util.stream.Collectors;

    @Controller
    @RequiredArgsConstructor
    @RequestMapping("/products")
    public class ProductController {

        private final ProductsService productService;
        private final CategoriesService categoryService;

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

            return "product/views";
        }


        // Tìm kiếm sản phẩm theo từ khóa
        @GetMapping("/search")
        public String searchProducts(@RequestParam(required = false) String keyword,
                                     @RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "8") int size,
                                     Model model) {
            if (keyword == null || keyword.trim().isEmpty()) {
                model.addAttribute("products", List.of());
                model.addAttribute("message", "Vui lòng nhập từ khóa tìm kiếm.");
                return "product/search-result";
            }

            Page<ProductDTO> resultPage = productService.searchProducts(keyword, PageRequest.of(page, size));
            model.addAttribute("products", resultPage.getContent());
            model.addAttribute("totalPages", resultPage.getTotalPages());
            model.addAttribute("currentPage", page);
            model.addAttribute("keyword", keyword);

            return "product/search-result";
        }


        // Lọc sản phẩm theo danh mục
        @GetMapping("/category/{id}")
        public String productsByCategory(@PathVariable("id") Long categoryId,
                                         @RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "8") int size,
                                         Model model) {

            Page<ProductDTO> resultPage = productService.getProductsByCategory(categoryId, PageRequest.of(page, size));
            model.addAttribute("products", resultPage.getContent());
            model.addAttribute("totalPages", resultPage.getTotalPages());
            model.addAttribute("currentPage", page);
            model.addAttribute("categoryId", categoryId);
            return "product/category-result"; // templates/product/category-result.html
        }



    }
