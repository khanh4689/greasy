package com.gearsy.gearsy.service.impl;

import com.gearsy.gearsy.dto.UserAdminDTO;
import com.gearsy.gearsy.entity.Users;
import com.gearsy.gearsy.entity.UserStatus;
import com.gearsy.gearsy.repository.UsersRepository;
import com.gearsy.gearsy.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UsersRepository usersRepository;

    @Override
    public Page<Users> findAll(Pageable pageable) {
        return usersRepository.findAll(pageable);
    }

    @Override
    public Page<UserAdminDTO> getAllUsersPaged(Pageable pageable) {
        return usersRepository.findAll(pageable).map(this::toUserAdminDTO);
    }

    @Override
    public Page<UserAdminDTO> searchUsers(String keyword, Pageable pageable) {
        return usersRepository.searchByKeyword(keyword, pageable).map(this::toUserAdminDTO);
    }

    @Override
    public Users getUserById(Long userId) {
        return usersRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + userId));
    }

    @Override
    public UserAdminDTO getUserAdminDTOById(Long userId) {
        Users user = getUserById(userId);
        return toUserAdminDTO(user);
    }

    @Override
    public void addUser(UserAdminDTO userDTO) {
        if (usersRepository.existsByEmail(userDTO.getEmail())) {
            throw new RuntimeException("Email đã tồn tại: " + userDTO.getEmail());
        }
        Users user = new Users();
        mapDTOToUser(userDTO, user);
        user.setCreatedAt(java.time.LocalDateTime.now());
        usersRepository.save(user);
    }
    public Users getUserByEmail(String email) {
        return usersRepository.findByEmail(email).orElse(null);
    }

    @Override
    public void updateUser(UserAdminDTO userDTO) {
        Users user = usersRepository.findById(userDTO.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID: " + userDTO.getId()));
        if (!user.getEmail().equals(userDTO.getEmail()) && usersRepository.existsByEmail(userDTO.getEmail())) {
            throw new RuntimeException("Email đã tồn tại: " + userDTO.getEmail());
        }
        mapDTOToUser(userDTO, user);
        usersRepository.save(user);
    }

    @Override
    public void toggleUserStatus(Long userId) {
        Users user = getUserById(userId);
        user.setStatus(user.getStatus() == UserStatus.Active ? UserStatus.Inactive : UserStatus.Active);
        usersRepository.save(user);
    }

    @Override
    public void deleteUser(Long userId) {
        Users user = getUserById(userId);
        usersRepository.delete(user);
    }

    private UserAdminDTO toUserAdminDTO(Users user) {
        UserAdminDTO dto = new UserAdminDTO();
        dto.setId(user.getUserId());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setPassword(user.getPassword());
        dto.setName(user.getName());
        dto.setAvatar(user.getAvatar());
        dto.setAddress(user.getAddress());
        dto.setRole(user.getRole());
        dto.setStatus(user.getStatus());
        return dto;
    }

    private void mapDTOToUser(UserAdminDTO dto, Users user) {
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setPassword(dto.getPassword());
        user.setName(dto.getName());
        user.setAvatar(dto.getAvatar());
        user.setAddress(dto.getAddress());
        user.setRole(dto.getRole() != null ? dto.getRole() : com.gearsy.gearsy.entity.UserRole.USER);
        user.setStatus(dto.getStatus() != null ? dto.getStatus() : UserStatus.Active);
    }
}