package com.gearsy.gearsy.service;

import com.gearsy.gearsy.entity.Favorites;
import com.gearsy.gearsy.entity.Products;
import com.gearsy.gearsy.entity.Users;

import java.util.List;

public interface FavoriteService {
    void addFavorite(Users user, Products product);
    boolean isFavoriteExist(Users user, Products product);
    List<Favorites> getFavoritesByUser(Users user);
    boolean deleteFavorite(Long favoriteId, Users user);
}
