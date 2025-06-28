package com.gearsy.gearsy.controller;


import com.gearsy.gearsy.entity.Categories;
import com.gearsy.gearsy.service.CategoriesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/home")
@RequiredArgsConstructor
public class HomeController {

    @RequestMapping({"/","/home/index"})
    public String home() {
        return"index";
    }

    @RequestMapping({"/admin","/admin/home/index"})
    public String admin() {
        return "redirect:/assets/admin/index.html";
    }
}
