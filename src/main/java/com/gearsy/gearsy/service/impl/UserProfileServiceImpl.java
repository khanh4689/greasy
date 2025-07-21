package com.gearsy.gearsy.service.impl;

import com.gearsy.gearsy.dto.UserUpdateDTO;
import com.gearsy.gearsy.entity.Users;
import com.gearsy.gearsy.repository.UsersRepository;
import com.gearsy.gearsy.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {

    private final UsersRepository usersRepository;

    @Override
    public Users getUserByEmail(String email) {
        return usersRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }

    @Override
    public void updateUserProfile(String email, UserUpdateDTO dto) {
        Users user = getUserByEmail(email);
        user.setName(dto.getName());
        user.setPhone(dto.getPhone());
        user.setAddress(dto.getAddress());

        MultipartFile file = dto.getAvatarFile();
        if (file != null && !file.isEmpty()) {
            // Đường dẫn tuyệt đối ngoài project
            String uploadDir = "C:\\uploads";

            // Tạo thư mục nếu chưa có
            File uploadPath = new File(uploadDir);
            if (!uploadPath.exists()) {
                uploadPath.mkdirs();
            }

            // Đặt tên file mới
            String newFileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

            // Đường dẫn file đầy đủ
            File destinationFile = new File(uploadPath, newFileName);

            try {
                // Lưu file
                file.transferTo(destinationFile);

                user.setAvatar("/uploads/" + newFileName);  // Phải map URL này qua controller
            } catch (IOException e) {
                throw new RuntimeException("Lỗi khi lưu file ảnh: " + e.getMessage());
            }
        }

        usersRepository.save(user);
    }
}
