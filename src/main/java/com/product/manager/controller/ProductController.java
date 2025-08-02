package com.product.manager.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/products")
public class ProductController {


    @PostMapping("/add")
    public String addProduct() {
        return "Product added successfully";
    }
}
