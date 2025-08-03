package com.product.manager.controller;

import com.product.manager.entity.Product;
import com.product.manager.repository.ProductRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/products")
@Tag(name = "Product Controller", description = "API for managing products")
public class ProductController {

    private final ProductRepository productRepository;

    @PostMapping("/add")
    @Operation(summary = "Add a new product", description = "Saves a new product to the database")
    public Product addProduct() {
        return productRepository.save(new Product());
    }

}
