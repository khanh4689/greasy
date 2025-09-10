package com.gearsy.gearsy.service.impl;

import com.gearsy.gearsy.entity.Favorites;
import com.gearsy.gearsy.entity.Products;
import com.gearsy.gearsy.entity.Users;
import com.gearsy.gearsy.repository.FavoriteRepository;
import com.gearsy.gearsy.service.FavoriteService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteRepository favoriteRepository;


    public FavoriteServiceImpl(FavoriteRepository favoriteRepository) {
        this.favoriteRepository = favoriteRepository;
    }

    @Override
    public void addFavorite(Users user, Products product) {
        Favorites favorite = new Favorites();
        favorite.setUser(user);
        favorite.setProduct(product);
        favorite.setCreatedAt(LocalDateTime.now());
        favoriteRepository.save(favorite);
    }

    @Override
    public boolean isFavoriteExist(Users user, Products product) {
        return favoriteRepository.existsByUserAndProduct(user, product);
    }

    @Override
    public List<Favorites> getFavoritesByUser(Users user) {
        return favoriteRepository.findByUser(user);
    }

    @Override
    public boolean deleteFavorite(Long favoriteId, Users user) {
        Optional<Favorites> favoriteOpt = favoriteRepository.findById(favoriteId);

        if (favoriteOpt.isPresent() && favoriteOpt.get().getUser().equals(user)) {
            favoriteRepository.delete(favoriteOpt.get());
            return true;
        }
        return false;
    }


}
