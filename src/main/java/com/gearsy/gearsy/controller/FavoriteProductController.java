package com.gearsy.gearsy.controller;

import com.gearsy.gearsy.entity.Favorites;
import com.gearsy.gearsy.entity.Products;
import com.gearsy.gearsy.entity.Users;
import com.gearsy.gearsy.service.FavoriteService;
import com.gearsy.gearsy.service.ProductsService;
import com.gearsy.gearsy.service.UserProfileService;
import com.gearsy.gearsy.repository.FavoriteRepository;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
public class FavoriteProductController {

    private final UserProfileService userProfileService;
    private final FavoriteRepository favoriteRepository;
    private final FavoriteService favoriteService;
    private final ProductsService productService;

    public FavoriteProductController(UserProfileService userProfileService,
                                     FavoriteRepository favoritesRepository,
                                     FavoriteService favoritesService,
                                     ProductsService productService) {
        this.userProfileService = userProfileService;
        this.favoriteRepository = favoritesRepository;
        this.favoriteService = favoritesService;
        this.productService = productService;
    }

    @GetMapping("/favorites")
    public String showFavorites(Model model, Principal principal) {
        String email = principal.getName();
        Users user = userProfileService.getUserByEmail(email);
        List<Favorites> favorites = favoriteRepository.findByUser(user);
        model.addAttribute("favorites", favorites);
        model.addAttribute("contentTemplate", "/favorite/list");
        return "layout";
    }

    @PostMapping("/user/favorites/add")
    public String addToFavorites(@RequestParam("productId") Long productId,
                                 Principal principal,
                                 RedirectAttributes redirectAttributes) {

        String email = principal.getName();
        Users user = userProfileService.getUserByEmail(email);
        Products product = productService.getProductById(productId);

        if (favoriteService.isFavoriteExist(user, product)) {
            redirectAttributes.addFlashAttribute("message", "Sản phẩm đã có trong danh sách yêu thích.");
        } else {
            favoriteService.addFavorite(user, product);
            redirectAttributes.addFlashAttribute("message", "Đã thêm vào danh sách yêu thích.");
        }

        return "redirect:/products/detail/" + productId;
    }

    @PostMapping("/favorites/delete/{id}")
    public String deleteFavorite(@PathVariable("id") Long favoriteId,
                                 Principal principal,
                                 RedirectAttributes redirectAttributes) {

        String email = principal.getName();
        Users user = userProfileService.getUserByEmail(email);

        // gọi service để kiểm tra & xóa theo favoriteId
        boolean deleted = favoriteService.deleteFavorite(favoriteId, user);

        if (deleted) {
            redirectAttributes.addFlashAttribute("message", "Đã xóa sản phẩm khỏi danh sách yêu thích.");
        } else {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy sản phẩm trong danh sách yêu thích.");
        }

        return "redirect:/favorites";
    }
}
