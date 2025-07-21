package com.gearsy.gearsy.service;

import com.gearsy.gearsy.entity.Users;

public interface ProfileService {
    Users getProfile(String email);
    Users updateProfile(String email, Users updatedUser);
}
