package com.product.manager.controller;

import com.product.manager.dto.GenericResponse;
import com.product.manager.dto.ProductDto;
import com.product.manager.dto.UpdateProductDto;
import com.product.manager.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/product")
@Tag(name = "Product Controller", description = "API for managing products")
public class ProductController {

    private final ProductService productService;

    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Add a new product", description = "Saves a new product to the database")
    public ResponseEntity<GenericResponse<Void>> addProduct(@RequestBody ProductDto request) {
        return ResponseEntity.ok(productService.addProduct(request));
    }

    @GetMapping("/{code}")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get product by code", description = "Retrieves a product by its unique code")
    public ResponseEntity<GenericResponse<ProductDto>> getProductByCode(@PathVariable Long code) {
        return ResponseEntity.ok(productService.getProductByCode(code));
    }

    @PutMapping("/change")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Change product by code", description = "Updates an existing product's details by its unique code")
    public ResponseEntity<GenericResponse<Void>> changeProduct(@RequestBody ProductDto request) {
        return ResponseEntity.ok(productService.changeProduct(request));
    }

    @PatchMapping("/{code}/update")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update product details", description = "Partially updates a product's details by its unique code")
    public ResponseEntity<GenericResponse<Void>> updateProduct(@PathVariable Long code, @RequestBody UpdateProductDto request) {
        return ResponseEntity.ok(productService.updateProduct(code, request));
    }

    @DeleteMapping("/{code}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Remove product by code", description = "Deletes a product by its unique code")
    public ResponseEntity<GenericResponse<Void>> removeProduct(@PathVariable Long code) {
        return ResponseEntity.ok(productService.removeProduct(code));
    }

    @GetMapping("/list")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get all products", description = "Retrieves a list of all products")
    public ResponseEntity<GenericResponse<List<ProductDto>>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @PutMapping("/{code}/change/{price}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Change product price", description = "Updates the price of a product by its unique code")
    public ResponseEntity<GenericResponse<Void>> changeProductPrice(@PathVariable Long code, @PathVariable Double price) {
        return ResponseEntity.ok(productService.changeProductPrice(code, price));
    }
}