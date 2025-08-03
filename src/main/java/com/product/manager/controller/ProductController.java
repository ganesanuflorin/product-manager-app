package com.product.manager.controller;

import com.product.manager.entity.Product;
import com.product.manager.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductRepository productRepository;

    @PostMapping("/add")
    public Product addProduct() {
        return productRepository.save(new Product());
    }

}
