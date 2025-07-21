package com.gearsy.gearsy.service;


import com.gearsy.gearsy.dto.UserUpdateDTO;
import com.gearsy.gearsy.entity.Users;

public interface UserProfileService {
    Users getUserByEmail(String email);
    void updateUserProfile(String email, UserUpdateDTO dto);
}
