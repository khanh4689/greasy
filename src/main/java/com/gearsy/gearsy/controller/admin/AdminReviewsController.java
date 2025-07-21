package com.gearsy.gearsy.controller.admin;


import com.gearsy.gearsy.entity.Reviews;
import com.gearsy.gearsy.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/reviews")
@RequiredArgsConstructor
public class AdminReviewsController {

    private final ReviewService reviewsService;

    @GetMapping
    public String listReviews(Model model) {
        model.addAttribute("reviews", reviewsService.getAllReviews());
        return "admin/reviews";
    }

    @PostMapping("/hide/{reviewId}")
    public String hideReview(@PathVariable Long reviewId) {
        reviewsService.hideReview(reviewId);
        return "redirect:/admin/reviews";
    }

    @PostMapping("/delete/{reviewId}")
    public String deleteReview(@PathVariable Long reviewId) {
        reviewsService.deleteReview(reviewId);
        return "redirect:/admin/reviews";
    }

    @GetMapping("/admin/reviews")
    public String showAllReviews(@RequestParam(name = "keyword", required = false) String keyword, Model model) {
        List<Reviews> reviews;

        if (keyword != null && !keyword.trim().isEmpty()) {
            reviews = reviewsService.searchReviews(keyword.trim());
            model.addAttribute("keyword", keyword);
        } else {
            reviews = reviewsService.getAllReviews();
        }

        model.addAttribute("reviews", reviews);
        return "admin/reviews";
    }

}
