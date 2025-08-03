package com.product.manager.dto;

import com.product.manager.exceptions.ProductValidationException;

public record ProductDto(String productName, Double price, Long quantity, String description, Long code) {

    public ProductDto {
        if (productName == null || productName.isBlank()) {
            throw new ProductValidationException("Product name cannot be null or blank");
        }
        if (price == null || price < 0) {
            throw new ProductValidationException("Price must be a non-negative number");
        }
        if (quantity == null || quantity < 0) {
            throw new ProductValidationException("Quantity must be a non-negative number");
        }
        if (code == null || code <= 0) {
            throw new ProductValidationException("Code must be a positive number");
        }
    }


}
