package com.gearsy.gearsy.controller.admin;

import com.gearsy.gearsy.dto.ProductAdminDTO;
import com.gearsy.gearsy.service.CategoriesService;
import com.gearsy.gearsy.service.ProductsService;
import com.gearsy.gearsy.service.SupplierService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/products")
public class AdminProductController {

    private final ProductsService productService;
    private final CategoriesService categoryService;
    private final SupplierService supplierService;

    private static final Logger logger = LoggerFactory.getLogger(AdminProductController.class);
    private static final String UPLOAD_DIR = "src/main/resources/static/images/";

    @GetMapping("/list")
    public String listProducts(@RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "10") int size,
                               @RequestParam(value = "keyword", required = false) String keyword,
                               Model model) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ProductAdminDTO> products = productService.getAllProductsPaged(keyword, pageable);

        model.addAttribute("products", products.getContent());
        model.addAttribute("totalPages", products.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("keyword", keyword); // để giữ lại từ khóa trên view
        model.addAttribute("contentTemplate", "admin/products/list");

        return "admin/layoutadmin";
    }


    @GetMapping("/add")
    public String showAddProductForm(Model model) {
        model.addAttribute("product", new ProductAdminDTO());
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("suppliers", supplierService.getAllSuppliers());
        return "admin/products/add";
    }

    @PostMapping("/add")
    public String addProduct(@ModelAttribute("product") ProductAdminDTO productDTO,
                             @RequestParam("imageFile") MultipartFile images,
                             Model model) {
        try {
            // Kiểm tra các trường bắt buộc
            if (productDTO.getName() == null || productDTO.getName().trim().isEmpty()) {
                model.addAttribute("error", "Tên sản phẩm không được để trống");
                populateModel(model);
                return "admin/products/layout";
            }
            if (productDTO.getPrice() == null || productDTO.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
                model.addAttribute("error", "Giá sản phẩm phải lớn hơn 0");
                populateModel(model);
                return "admin/products/layout";
            }
            if (productDTO.getStock() == null || productDTO.getStock() < 0) {
                model.addAttribute("error", "Số lượng tồn kho phải lớn hơn hoặc bằng 0");
                populateModel(model);
                return "admin/products/layout";
            }
            if (productDTO.getCategoryId() == null) {
                model.addAttribute("error", "Danh mục không được để trống");
                populateModel(model);
                return "admin/products/layout";
            }
            if (productDTO.getSupplierId() == null) {
                model.addAttribute("error", "Nhà cung cấp không được để trống");
                populateModel(model);
                return "admin/products/layout";
            }

            // Kiểm tra file ảnh
            if (images.isEmpty()) {
                model.addAttribute("error", "Vui lòng chọn một ảnh");
                populateModel(model);
                return "admin/products/layout";
            }
            if (!images.getContentType().startsWith("image/")) {
                model.addAttribute("error", "Vui lòng chọn một file ảnh hợp lệ (jpg, png, v.v.)");
                populateModel(model);
                return "admin/products/layout";
            }
            if (images.getSize() > 5 * 1024 * 1024) { // 5MB
                model.addAttribute("error", "Kích thước ảnh không được vượt quá 5MB");
                populateModel(model);
                return "admin/products/layout";
            }

            // Xử lý file upload
            String fileName = images.getOriginalFilename();
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            Path filePath = uploadPath.resolve(fileName);
            try {
                Files.copy(images.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                productDTO.setImages(fileName); // Gán tên file vào DTO
                logger.info("Đã upload file ảnh: {}", fileName);
            } catch (IOException e) {
                logger.error("Lỗi khi lưu file ảnh: {}", e.getMessage(), e);
                model.addAttribute("error", "Không thể tải lên ảnh: " + e.getMessage());
                populateModel(model);
                return "admin/products/layout";
            }

            productService.addProduct(productDTO);
            logger.info("Đã thêm sản phẩm thành công: {}", productDTO.getName());
            return "redirect:/admin/products/list";
        } catch (Exception e) {
            logger.error("Lỗi khi thêm sản phẩm: {}", e.getMessage(), e);
            model.addAttribute("error", "Không thể thêm sản phẩm: " + e.getMessage());
            populateModel(model);
            return "admin/products/layout";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditProductForm(@PathVariable("id") Long productId, Model model) {
        ProductAdminDTO product = productService.getProductAdminDTOById(productId);
        model.addAttribute("product", product);
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("suppliers", supplierService.getAllSuppliers());
        return "admin/products/edit";
    }

    @PostMapping("/edit/{id}")
    public String editProduct(@PathVariable("id") Long productId,
                              @ModelAttribute("product") ProductAdminDTO productDTO,
                              @RequestParam("imageFile") MultipartFile images,
                              Model model) {
        try {
            // Kiểm tra các trường bắt buộc
            if (productDTO.getName() == null || productDTO.getName().trim().isEmpty()) {
                model.addAttribute("error", "Tên sản phẩm không được để trống");
                populateModel(model);
                return "admin/products/layout";
            }
            if (productDTO.getPrice() == null || productDTO.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
                model.addAttribute("error", "Giá sản phẩm phải lớn hơn 0");
                populateModel(model);
                return "admin/products/layout";
            }
            if (productDTO.getStock() == null || productDTO.getStock() < 0) {
                model.addAttribute("error", "Số lượng tồn kho phải lớn hơn hoặc bằng 0");
                populateModel(model);
                return "admin/products/layout";
            }
            if (productDTO.getCategoryId() == null) {
                model.addAttribute("error", "Danh mục không được để trống");
                populateModel(model);
                return "admin/products/layout";
            }
            if (productDTO.getSupplierId() == null) {
                model.addAttribute("error", "Nhà cung cấp không được để trống");
                populateModel(model);
                return "admin/products/layout";
            }

            // Lấy thông tin sản phẩm hiện tại
            ProductAdminDTO existingProduct = productService.getProductAdminDTOById(productId);
            String oldImage = existingProduct.getImages();

            // Xử lý file upload
            if (!images.isEmpty()) {
                if (!images.getContentType().startsWith("image/")) {
                    model.addAttribute("error", "Vui lòng chọn một file ảnh hợp lệ (jpg, png, v.v.)");
                    populateModel(model);
                    return "admin/products/layout";
                }
                if (images.getSize() > 5 * 1024 * 1024) { // 5MB
                    model.addAttribute("error", "Kích thước ảnh không được vượt quá 5MB");
                    populateModel(model);
                    return "admin/products/layout";
                }

                String fileName = images.getOriginalFilename();
                Path uploadPath = Paths.get(UPLOAD_DIR);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(images.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                productDTO.setImages(fileName);
                logger.info("Đã upload file ảnh mới: {}", fileName);

                // Xóa ảnh cũ nếu tồn tại
                if (oldImage != null && !oldImage.isEmpty()) {
                    Path oldImagePath = Paths.get(UPLOAD_DIR, oldImage);
                    try {
                        Files.deleteIfExists(oldImagePath);
                        logger.info("Đã xóa ảnh cũ: {}", oldImage);
                    } catch (IOException e) {
                        logger.error("Lỗi khi xóa ảnh cũ: {}", e.getMessage(), e);
                    }
                }
            } else {
                // Giữ nguyên ảnh cũ nếu không upload ảnh mới
                productDTO.setImages(oldImage);
            }

            productDTO.setId(productId);
            productService.updateProduct(productDTO);
            logger.info("Đã cập nhật sản phẩm thành công: {}", productDTO.getName());
            return "redirect:/admin/products/list";
        } catch (Exception e) {
            logger.error("Lỗi khi cập nhật sản phẩm: {}", e.getMessage(), e);
            model.addAttribute("error", "Không thể cập nhật sản phẩm: " + e.getMessage());
            populateModel(model);
            return "admin/products/layout";
        }
    }

    @PostMapping("/hide/{id}")
    public String hideProduct(@PathVariable("id") Long productId) {
        productService.hideProduct(productId);
        return "redirect:/admin/products/list";
    }

    @PostMapping("/unhide/{id}")
    public String unhideProduct(@PathVariable("id") Long productId) {
        productService.unhideProduct(productId);
        return "redirect:/admin/products/list";
    }

    @PostMapping("/update-stock/{id}")
    public String updateStock(@PathVariable("id") Long productId,
                              @RequestParam("stock") Integer stock) {
        productService.updateStock(productId, stock);
        return "redirect:/admin/products/list";
    }



    @PostMapping("/delete/{id}")
    public String deleteProduct(@PathVariable("id") Long productId, Model model) {
        try {
            ProductAdminDTO productDTO = productService.getProductAdminDTOById(productId);
            String image = productDTO.getImages();
            productService.deleteProduct(productId);

            // Xóa ảnh liên quan
            if (image != null && !image.isEmpty()) {
                Path imagePath = Paths.get(UPLOAD_DIR, image);
                try {
                    Files.deleteIfExists(imagePath);
                    logger.info("Đã xóa ảnh sản phẩm: {}", image);
                } catch (IOException e) {
                    logger.error("Lỗi khi xóa ảnh sản phẩm: {}", e.getMessage(), e);
                }
            }

            logger.info("Đã xóa sản phẩm thành công: {}", productId);
            return "redirect:/admin/products/list";

        } catch (DataIntegrityViolationException e) {
            logger.error("Không thể xóa sản phẩm vì đang được sử dụng trong đơn hàng.", e);
            model.addAttribute("error", "Không thể xóa sản phẩm vì sản phẩm đã có trong đơn hàng.");

        } catch (Exception e) {
            logger.error("Lỗi khi xóa sản phẩm: {}", e.getMessage(), e);
            model.addAttribute("error", "Đã xảy ra lỗi khi xóa sản phẩm: " + e.getMessage());
        }

        // Load lại danh sách sản phẩm sau lỗi
        Pageable pageable = PageRequest.of(0, 10);
        Page<ProductAdminDTO> products = productService.getAllProductsPaged(pageable);
        model.addAttribute("products", products.getContent());
        model.addAttribute("totalPages", products.getTotalPages());
        model.addAttribute("currentPage", 0);
        model.addAttribute("contentTemplate", "admin/products/list");

        return "admin/layoutadmin";
    }


    private void populateModel(Model model) {
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("suppliers", supplierService.getAllSuppliers());
        model.addAttribute("contentTemplate", "admin/products/add");
    }
}
