package com.gearsy.gearsy.service;

import com.gearsy.gearsy.dto.UserAdminDTO;
import com.gearsy.gearsy.entity.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    Page<Users> findAll(Pageable pageable);
    Page<UserAdminDTO> getAllUsersPaged(Pageable pageable);
    Page<UserAdminDTO> searchUsers(String keyword, Pageable pageable);
    Users getUserById(Long userId);
    UserAdminDTO getUserAdminDTOById(Long userId);
    void addUser(UserAdminDTO userDTO);
    void updateUser(UserAdminDTO userDTO);
    void toggleUserStatus(Long userId);
    void deleteUser(Long userId);
    Users getUserByEmail(String email);
}