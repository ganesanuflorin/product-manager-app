package com.product.manager.dto;

import com.product.manager.exceptions.ProductValidationException;
import org.springframework.http.HttpStatus;

public record ProductDto(String productName, Double price, Long quantity, String description, Long code) {

    public ProductDto {
        if (productName == null || productName.isBlank()) {
            throw new ProductValidationException("Product name cannot be null or blank", HttpStatus.BAD_REQUEST);
        }
        if (price == null || price < 0) {
            throw new ProductValidationException("Price must be a non-negative number", HttpStatus.BAD_REQUEST);
        }
        if (quantity == null || quantity < 0) {
            throw new ProductValidationException("Quantity must be a non-negative number", HttpStatus.BAD_REQUEST);
        }
        if (code == null || code <= 0) {
            throw new ProductValidationException("Code must be a positive number", HttpStatus.BAD_REQUEST);
        }
    }


}
