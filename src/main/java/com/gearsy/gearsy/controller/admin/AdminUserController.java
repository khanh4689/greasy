package com.gearsy.gearsy.controller.admin;

import com.gearsy.gearsy.dto.UserAdminDTO;
import com.gearsy.gearsy.entity.UserRole;
import com.gearsy.gearsy.entity.UserStatus;
import com.gearsy.gearsy.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/users")
public class AdminUserController {

    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(AdminUserController.class);
    private static final String UPLOAD_DIR = "src/main/resources/static/images/";

    @GetMapping("/list")
    public String listUsers(@RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "10") int size,
                            @RequestParam(value = "search", required = false) String search,
                            Model model) {
        logger.info("Hiển thị danh sách người dùng, trang: {}, tìm kiếm: {}", page, search);
        Pageable pageable = PageRequest.of(page, size);
        Page<UserAdminDTO> users = search != null && !search.isEmpty()
                ? userService.searchUsers(search, pageable)
                : userService.getAllUsersPaged(pageable);

        model.addAttribute("users", users.getContent());
        model.addAttribute("totalPages", users.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("search", search);
        model.addAttribute("contentTemplate", "admin/users/list");
        return "admin/layoutadmin";
    }

    @GetMapping("/add")
    public String showAddUserForm(Model model) {
        logger.info("Hiển thị form thêm người dùng");
        model.addAttribute("user", new UserAdminDTO());
        model.addAttribute("roles", UserRole.values());
        model.addAttribute("statuses", UserStatus.values());
        model.addAttribute("contentTemplate", "admin/users/add");
        return "admin/users/layout";
    }

    @PostMapping("/add")
    public String addUser(@ModelAttribute("user") UserAdminDTO userDTO,
                          @RequestParam("avatarFile") MultipartFile avatar,
                          Model model) {
        try {
            logger.info("Thêm người dùng mới, email: {}", userDTO.getEmail());
            // Kiểm tra các trường bắt buộc
            if (userDTO.getEmail() == null || userDTO.getEmail().trim().isEmpty()) {
                logger.warn("Email không được để trống");
                model.addAttribute("error", "Email không được để trống");
                populateModel(model, userDTO);
                return "admin/users/layout";
            }
            if (userDTO.getPassword() == null || userDTO.getPassword().trim().isEmpty()) {
                logger.warn("Mật khẩu không được để trống");
                model.addAttribute("error", "Mật khẩu không được để trống");
                populateModel(model, userDTO);
                return "admin/users/layout";
            }

            // Kiểm tra file ảnh
            if (!avatar.isEmpty()) {
                if (!avatar.getContentType().startsWith("image/")) {
                    logger.warn("File ảnh không hợp lệ: {}", avatar.getContentType());
                    model.addAttribute("error", "Vui lòng chọn một file ảnh hợp lệ (jpg, png, v.v.)");
                    populateModel(model, userDTO);
                    return "admin/users/layout";
                }
                if (avatar.getSize() > 5 * 1024 * 1024) { // 5MB
                    logger.warn("Kích thước ảnh vượt quá 5MB: {}", avatar.getSize());
                    model.addAttribute("error", "Kích thước ảnh không được vượt quá 5MB");
                    populateModel(model, userDTO);
                    return "admin/users/layout";
                }

                String fileName = System.currentTimeMillis() + "_" + avatar.getOriginalFilename();
                Path uploadPath = Paths.get(UPLOAD_DIR);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(avatar.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                userDTO.setAvatar(fileName);
                logger.info("Đã upload file ảnh: {}", fileName);
            }

            userService.addUser(userDTO);
            logger.info("Đã thêm người dùng thành công: {}", userDTO.getEmail());
            return "redirect:/admin/users/list";
        } catch (IOException e) {
            logger.error("Lỗi khi lưu file ảnh: {}", e.getMessage(), e);
            model.addAttribute("error", "Không thể tải lên ảnh: " + e.getMessage());
            populateModel(model, userDTO);
            return "admin/users/layout";
        } catch (Exception e) {
            logger.error("Lỗi khi thêm người dùng: {}", e.getMessage(), e);
            model.addAttribute("error", "Không thể thêm người dùng: " + e.getMessage());
            populateModel(model, userDTO);
            return "admin/users/layout";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditUserForm(@PathVariable("id") Long userId, Model model) {
        try {
            logger.info("Hiển thị form sửa người dùng, ID: {}", userId);
            UserAdminDTO user = userService.getUserAdminDTOById(userId);
            model.addAttribute("user", user);
            model.addAttribute("roles", UserRole.values());
            model.addAttribute("statuses", UserStatus.values());
            model.addAttribute("contentTemplate", "admin/users/add");
            return "admin/users/layout";
        } catch (Exception e) {
            logger.error("Lỗi khi lấy thông tin người dùng: {}", e.getMessage(), e);
            model.addAttribute("error", "Không thể lấy thông tin người dùng: " + e.getMessage());
            Pageable pageable = PageRequest.of(0, 10);
            Page<UserAdminDTO> users = userService.getAllUsersPaged(pageable);
            model.addAttribute("users", users.getContent());
            model.addAttribute("totalPages", users.getTotalPages());
            model.addAttribute("currentPage", 0);
            model.addAttribute("contentTemplate", "admin/users/list");
            return "admin/users/layout";
        }
    }

    @PostMapping("/edit/{id}")
    public String editUser(@PathVariable("id") Long userId,
                           @ModelAttribute("user") UserAdminDTO userDTO,
                           @RequestParam("avatarFile") MultipartFile avatar,
                           Model model) {
        try {
            logger.info("Cập nhật người dùng, ID: {}, email: {}", userId, userDTO.getEmail());
            // Kiểm tra các trường bắt buộc
            if (userDTO.getEmail() == null || userDTO.getEmail().trim().isEmpty()) {
                logger.warn("Email không được để trống");
                model.addAttribute("error", "Email không được để trống");
                populateModel(model, userDTO);
                return "admin/users/layout";
            }
            if (userDTO.getPassword() == null || userDTO.getPassword().trim().isEmpty()) {
                logger.warn("Mật khẩu không được để trống");
                model.addAttribute("error", "Mật khẩu không được để trống");
                populateModel(model, userDTO);
                return "admin/users/layout";
            }

            // Lấy thông tin người dùng hiện tại
            UserAdminDTO existingUser = userService.getUserAdminDTOById(userId);
            String oldAvatar = existingUser.getAvatar();
            logger.debug("Ảnh cũ: {}", oldAvatar);

            // Xử lý file upload
            if (!avatar.isEmpty()) {
                if (!avatar.getContentType().startsWith("image/")) {
                    logger.warn("File ảnh không hợp lệ: {}", avatar.getContentType());
                    model.addAttribute("error", "Vui lòng chọn một file ảnh hợp lệ (jpg, png, v.v.)");
                    populateModel(model, userDTO);
                    return "admin/users/layout";
                }
                if (avatar.getSize() > 5 * 1024 * 1024) { // 5MB
                    logger.warn("Kích thước ảnh vượt quá 5MB: {}", avatar.getSize());
                    model.addAttribute("error", "Kích thước ảnh không được vượt quá 5MB");
                    populateModel(model, userDTO);
                    return "admin/users/layout";
                }

                String fileName = System.currentTimeMillis() + "_" + avatar.getOriginalFilename();
                Path uploadPath = Paths.get(UPLOAD_DIR);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(avatar.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                userDTO.setAvatar(fileName);
                logger.info("Đã upload file ảnh mới: {}", fileName);

                // Xóa ảnh cũ nếu tồn tại
                if (oldAvatar != null && !oldAvatar.isEmpty()) {
                    Path oldAvatarPath = Paths.get(UPLOAD_DIR, oldAvatar);
                    try {
                        Files.deleteIfExists(oldAvatarPath);
                        logger.info("Đã xóa ảnh cũ: {}", oldAvatar);
                    } catch (IOException e) {
                        logger.error("Lỗi khi xóa ảnh cũ: {}", e.getMessage(), e);
                    }
                }
            } else {
                userDTO.setAvatar(oldAvatar);
                logger.debug("Giữ ảnh cũ: {}", oldAvatar);
            }

            userDTO.setId(userId);
            userService.updateUser(userDTO);
            logger.info("Đã cập nhật người dùng thành công: {}", userDTO.getEmail());
            return "redirect:/admin/users/list";
        } catch (IOException e) {
            logger.error("Lỗi khi lưu file ảnh: {}", e.getMessage(), e);
            model.addAttribute("error", "Không thể tải lên ảnh: " + e.getMessage());
            populateModel(model, userDTO);
            return "admin/users/layout";
        } catch (Exception e) {
            logger.error("Lỗi khi cập nhật người dùng: {}", e.getMessage(), e);
            model.addAttribute("error", "Không thể cập nhật người dùng: " + e.getMessage());
            populateModel(model, userDTO);
            return "admin/users/layout";
        }
    }

    @PostMapping("/toggle/{id}")
    public String toggleUserStatus(@PathVariable("id") Long userId) {
        logger.info("Chuyển đổi trạng thái người dùng, ID: {}", userId);
        userService.toggleUserStatus(userId);
        return "redirect:/admin/users/list";
    }

    @PostMapping("/delete/{id}")
    public String deleteUser(@PathVariable("id") Long userId, Model model) {
        try {
            logger.info("Xóa người dùng, ID: {}", userId);
            UserAdminDTO userDTO = userService.getUserAdminDTOById(userId);
            String avatar = userDTO.getAvatar();
            userService.deleteUser(userId);

            // Xóa ảnh liên quan
            if (avatar != null && !avatar.isEmpty()) {
                Path avatarPath = Paths.get(UPLOAD_DIR, avatar);
                try {
                    Files.deleteIfExists(avatarPath);
                    logger.info("Đã xóa ảnh người dùng: {}", avatar);
                } catch (IOException e) {
                    logger.error("Lỗi khi xóa ảnh người dùng: {}", e.getMessage(), e);
                }
            }

            logger.info("Đã xóa người dùng thành công: {}", userId);
            return "redirect:/admin/users/list";
        } catch (Exception e) {
            logger.error("Lỗi khi xóa người dùng: {}", e.getMessage(), e);
            model.addAttribute("error", "Không thể xóa người dùng: " + e.getMessage());
            Pageable pageable = PageRequest.of(0, 10);
            Page<UserAdminDTO> users = userService.getAllUsersPaged(pageable);
            model.addAttribute("users", users.getContent());
            model.addAttribute("totalPages", users.getTotalPages());
            model.addAttribute("currentPage", 0);
            model.addAttribute("contentTemplate", "admin/users/list");
            return "admin/users/layout";
        }
    }

    private void populateModel(Model model, UserAdminDTO userDTO) {
        model.addAttribute("user", userDTO);
        model.addAttribute("roles", UserRole.values());
        model.addAttribute("statuses", UserStatus.values());
        model.addAttribute("contentTemplate", "admin/users/add");
    }
}